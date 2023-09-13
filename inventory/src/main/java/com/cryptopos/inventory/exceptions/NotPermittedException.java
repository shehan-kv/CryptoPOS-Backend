package com.cryptopos.inventory.exceptions;

public class NotPermittedException extends RuntimeException {

    public NotPermittedException() {
        super("Not Permitted");
    }
}
