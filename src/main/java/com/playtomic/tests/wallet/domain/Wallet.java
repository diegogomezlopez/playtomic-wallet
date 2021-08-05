package com.playtomic.tests.wallet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public final class Wallet {

    private final String id;
    private final BigDecimal balance;
}
