package com.playtomic.tests.wallet.exception;

public class WalletDuplicateKeyException extends RuntimeException {

    public WalletDuplicateKeyException() {
        super("Duplicated primary key.");
    }
}
