package com.cryptopos.user.service.user;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cryptopos.user.dto.EmployeeBranchesAddRequest;
import com.cryptopos.user.dto.EmployeeCreateRequest;
import com.cryptopos.user.dto.EmployeeUpdateRequest;
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
                                .findByName(createRequest.role())
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

                                    return userRepository.save(newEmployee);
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
                                    updateRequest.lastName(), role.id())
                            .flatMap(result -> amqpService.setUserBranches(
                                    new EmployeeBranchesAddRequest(employeeId, updateRequest.branches())));
                })
                .map(result -> true);
    }

}
