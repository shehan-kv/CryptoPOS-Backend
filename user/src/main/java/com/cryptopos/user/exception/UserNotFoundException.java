package com.cryptopos.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User Not Found");
    }
}
