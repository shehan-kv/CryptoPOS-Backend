package com.cryptopos.user.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cryptopos.user.dto.SignUpRequest;
import com.cryptopos.user.entity.User;
import com.cryptopos.user.repository.RoleRepository;
import com.cryptopos.user.repository.UserRepository;

import reactor.core.publisher.Mono;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository uRepository, RoleRepository rRepository, PasswordEncoder encoder) {
        this.userRepository = uRepository;
        this.roleRepository = rRepository;
        this.passwordEncoder = encoder;
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
                                .findByName("GLOBAL_ADMINISTRATOR")
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

}
