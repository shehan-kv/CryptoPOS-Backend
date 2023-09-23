package com.cryptopos.orders.exceptions;

public class NoItemsException extends RuntimeException {

    public NoItemsException() {
        super("No Items");
    }
}
