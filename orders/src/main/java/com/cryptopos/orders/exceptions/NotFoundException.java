package com.cryptopos.orders.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Not Found");
    }
}
