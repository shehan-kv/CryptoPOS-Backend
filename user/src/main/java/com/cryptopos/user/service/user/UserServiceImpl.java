package com.cryptopos.user.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cryptopos.user.dto.EmployeeCreateRequest;
import com.cryptopos.user.dto.EmployeeResponse;
import com.cryptopos.user.dto.EmployeeWithBranchesResponse;
import com.cryptopos.user.dto.EmployeeUpdateRequest;
import com.cryptopos.user.dto.Page;
import com.cryptopos.user.dto.SignUpRequest;
import com.cryptopos.user.entity.Role;
import com.cryptopos.user.entity.User;
import com.cryptopos.user.exception.NotPermittedException;
import com.cryptopos.user.exception.UserExistsException;
import com.cryptopos.user.exception.UserNoBranchException;
import com.cryptopos.user.exception.UserNotFoundException;
import com.cryptopos.user.repository.RoleRepository;
import com.cryptopos.user.repository.UserRepository;
import com.cryptopos.user.service.amqp.AmqpService;

import reactor.core.publisher.Mono;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpService amqpService;

    public UserServiceImpl(UserRepository uRepository, RoleRepository rRepository, PasswordEncoder encoder,
            AmqpService amqpService) {
        this.userRepository = uRepository;
        this.roleRepository = rRepository;
        this.passwordEncoder = encoder;
        this.amqpService = amqpService;
    }

    @Override
    public Mono<Boolean> signUp(SignUpRequest signUpRequest) {

        if (!signUpRequest.password().equals(signUpRequest.confirmPassword())) {
            return Mono.just(false);
        }

        return userRepository
                .findByEmail(signUpRequest.email())
                .flatMap(existingUser -> Mono.just(false))
                .switchIfEmpty(
                        roleRepository
                                .findByName("ROLE_GLOBAL_ADMINISTRATOR")
                                .flatMap(role -> {

                                    var user = new User(
                                            null,
                                            signUpRequest.firstName(),
                                            signUpRequest.lastName(),
                                            signUpRequest.email(),
                                            passwordEncoder.encode(signUpRequest.password()),
                                            true,
                                            true,
                                            role.id());

                                    return userRepository.save(user)
                                            .flatMap(savedUser -> Mono.just(true))
                                            .onErrorResume(error -> Mono.just(false));

                                })
                                .switchIfEmpty(Mono.just(false)));
    }

    @Override
    public Mono<Boolean> createEmployee(EmployeeCreateRequest createRequest) {

        return userRepository
                .findByEmail(createRequest.email())
                .map(existingUser -> {
                    throw new UserExistsException();
                })
                .switchIfEmpty(
                        roleRepository
                                .findByName(createRequest.role().toString())
                                .zipWith(ReactiveSecurityContextHolder
                                        .getContext()
                                        .map(context -> context.getAuthentication().getName()))
                                .flatMap(tuple -> {
                                    Role role = tuple.getT1();
                                    Long userId = Long.parseLong(tuple.getT2());

                                    return amqpService
                                            .getUserBranches(userId)
                                            .map(branchList -> {

                                                if (createRequest.branches().size() == 0) {
                                                    throw new UserNoBranchException();
                                                }

                                                if (!CollectionUtils.containsAny(branchList,
                                                        createRequest.branches())) {
                                                    throw new NotPermittedException();
                                                }

                                                return role;
                                            });

                                })
                                .flatMap(role -> {

                                    User newEmployee = new User(
                                            null,
                                            createRequest.firstName(),
                                            createRequest.lastName(),
                                            createRequest.email(),
                                            passwordEncoder.encode(createRequest.password()),
                                            true,
                                            true,
                                            role.id());

                                    return userRepository
                                            .save(newEmployee)
                                            .flatMap(savedUser -> {
                                                HashMap<Long, List<Long>> requestMap = new HashMap<>();
                                                requestMap.put(savedUser.id(), createRequest.branches());
                                                return amqpService.setUserBranches(requestMap);
                                            });
                                }))
                .flatMap(savedUser -> Mono.just(true));
    }

    @Override
    public Mono<Boolean> updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest) {

        if (updateRequest.branches().size() == 0) {
            return Mono.error(new UserNoBranchException());
        }

        return userRepository
                .findById(employeeId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .zipWith(roleRepository.findByName(updateRequest.role()))
                .flatMap(tuple -> {
                    Role role = tuple.getT2();

                    return userRepository
                            .updateEmployee(employeeId,
                                    updateRequest.firstName(),
                                    updateRequest.lastName(),
                                    updateRequest.isActive(),
                                    role.id())
                            .flatMap(result -> {

                                HashMap<Long, List<Long>> requestMap = new HashMap<>();
                                requestMap.put(employeeId, updateRequest.branches());
                                return amqpService.setUserBranches(requestMap);
                            });
                })
                .map(result -> true);
    }

    @Override
    public Mono<EmployeeWithBranchesResponse> getEmployee(Long employeeId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> amqpService.getUserBranches(Long.parseLong(userId)))
                .zipWith(amqpService.getUserBranches(employeeId))
                .map(tuple -> {

                    if (!CollectionUtils.containsAny(tuple.getT2(), tuple.getT1())) {
                        throw new NotPermittedException();
                    }

                    return tuple.getT2();
                })
                .zipWith(userRepository.findById(employeeId))
                .flatMap(tuple -> {

                    User employee = tuple.getT2();

                    return roleRepository
                            .findById(employee.roleId())
                            .map(role -> new EmployeeWithBranchesResponse(
                                    employee.id(),
                                    employee.firstName(),
                                    employee.lastName(),
                                    employee.isActive(),
                                    tuple.getT1(), // Branches List
                                    role.name()));

                });
    }

    @Override
    public Mono<Page<EmployeeResponse>> getEmployees(Optional<String> pageNum, Optional<String> pageSize) {

        Long pageNumLong = Math.max(Long.parseLong(pageNum.orElse("1")), 1);
        Long pageSizeLong = Math.max(Long.parseLong(pageSize.orElse("20")), 1);
        Long offset = (pageNumLong - 1) * pageSizeLong;

        return ReactiveSecurityContextHolder
                .getContext()
                .map(context -> context.getAuthentication().getName())
                .flatMap(userId -> amqpService
                        .getEmployeeBranchInfo(List.of(Long.parseLong(userId), offset, pageSizeLong)))
                .flatMap(employeeInfo -> {

                    Long totalEmployees = employeeInfo.keySet().iterator().next();
                    List<Long> employeeList = employeeInfo.values().iterator().next();

                    Long pageCount = (long) Math.max((int) (Math.ceil(totalEmployees) / pageSizeLong), 1);

                    return userRepository
                            .findAllEmployeesById(employeeList).collectList()
                            .map(employeeDetails -> new Page<EmployeeResponse>(pageNumLong, pageSizeLong, pageCount,
                                    employeeDetails));
                });
    }

    @Override
    public Mono<Boolean> isLoggedIn() {
        return Mono.just(true);
    }

}
