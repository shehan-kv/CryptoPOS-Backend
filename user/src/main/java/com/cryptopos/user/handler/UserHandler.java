package com.cryptopos.user.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cryptopos.user.dto.SignUpRequest;
import com.cryptopos.user.service.user.UserService;

import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService uService) {
        this.userService = uService;
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request
                .bodyToMono(SignUpRequest.class)
                .flatMap(signUpRequest -> userService.signUp(signUpRequest))
                .flatMap(result -> result ? ServerResponse.ok().build()
                        : ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
