package com.cryptopos.user.exception;

public class UserExistsException extends RuntimeException {

    public UserExistsException() {
        super("User Exists");
    }
}
