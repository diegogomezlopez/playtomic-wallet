package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.StripeServiceException;
import com.playtomic.tests.wallet.exception.WalletChargeException;
import com.playtomic.tests.wallet.exception.WalletRechargeException;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final WalletRepository walletRepository;
    private final StripeService stripeService;
    private final WalletService walletService;

    public BalanceServiceImpl(final WalletRepository walletRepository, final StripeService stripeService, final WalletService walletService) {
        this.walletRepository = walletRepository;
        this.stripeService = stripeService;
        this.walletService = walletService;
    }

    @Override
    @Transactional
    public void recharge(final Long walletId, final String creditCardNumber, final BigDecimal amount) {
        Wallet wallet = walletService.findWalletById(walletId);
        chargeToCreditCard(creditCardNumber, amount);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void charge(Long walletId, BigDecimal amount) {
        Wallet wallet = walletService.findWalletById(walletId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new WalletChargeException("Not enough credit in your wallet.");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    private void chargeToCreditCard(final String creditCardNumber, final BigDecimal amount) {
        try {
            stripeService.charge(creditCardNumber, amount);
        } catch (StripeServiceException exception) {
            throw new WalletRechargeException("Recharge failed. Minimum amount rechargeable is 10.");
        }
    }
}
