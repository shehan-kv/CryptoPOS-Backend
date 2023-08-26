package com.cryptopos.user.handler;

import org.springframework.stereotype.Component;

import com.cryptopos.user.service.user.UserService;

@Component
public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService uService) {
        this.userService = uService;
    }
}
