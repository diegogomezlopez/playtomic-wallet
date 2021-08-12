package com.playtomic.tests.wallet.service;

import java.math.BigDecimal;

public interface BalanceService {

    void recharge(final Long walletId, final String creditCardNumber, final BigDecimal amount);

    void charge(final Long walletId, final BigDecimal amount);
}
