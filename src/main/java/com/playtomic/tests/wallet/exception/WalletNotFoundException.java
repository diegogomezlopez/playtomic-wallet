package com.playtomic.tests.wallet.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(final String message) {
        super(message);
    }
}
