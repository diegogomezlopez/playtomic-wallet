package com.playtomic.tests.wallet.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException() {
        super("Wallet not found");
    }

}
