package com.cryptopos.user.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.cryptopos.user.repository.UserRepository;

import reactor.core.publisher.Mono;

public class AppReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository
                .findAuthDetailsByEmail(username)
                .flatMap(user -> {
                    UserDetails userDetails = User
                            .builder()
                            .username(user.id().toString())
                            .password(user.password())
                            .accountExpired(false)
                            .disabled(!user.isActive())
                            .accountLocked(false)
                            .authorities(
                                    List.of(new SimpleGrantedAuthority(user.role().toString())))
                            .build();

                    return Mono.just(userDetails);
                });
    }

}
