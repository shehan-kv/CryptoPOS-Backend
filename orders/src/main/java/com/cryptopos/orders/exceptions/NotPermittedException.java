package com.cryptopos.orders.exceptions;

public class NotPermittedException extends RuntimeException {

    public NotPermittedException() {
        super("Not Permitted");
    }
}
