package com.cryptopos.user.exception;

public class NotPermittedException extends RuntimeException {

    public NotPermittedException() {
        super("Not Permitted");
    }
}
