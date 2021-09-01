package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.StripeServiceException;
import com.playtomic.tests.wallet.exception.WalletChargeException;
import com.playtomic.tests.wallet.exception.WalletRechargeException;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    private static final Long ID = 1L;
    private static final String CREDIT_CARD_NUMBER = "1234";
    private static final BigDecimal AMOUNT = new BigDecimal(100);

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Mock
    private WalletService walletService;

    @Mock
    private StripeService stripeService;

    @Mock
    private WalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    public void setup() {
        wallet = Wallet.builder()
                .id(ID)
                .balance(new BigDecimal(1000))
                .build();
        when(walletService.findWalletById(ID)).thenReturn(wallet);
    }

    @Test
    public void recharge_whenConditionsOk_thenRechargeWallet() throws StripeServiceException {
        balanceService.recharge(ID, CREDIT_CARD_NUMBER, AMOUNT);

        verify(walletRepository, times(1)).save(wallet);
        verify(stripeService, times(1)).charge(CREDIT_CARD_NUMBER, AMOUNT);
        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(1100));
    }

    @Test
    public void recharge_whenNotMinimumRechargeLimit_thenThrowWalletRechargeException() throws StripeServiceException {
        doThrow(new StripeServiceException()).when(stripeService).charge(CREDIT_CARD_NUMBER, AMOUNT);

        Assertions.assertThrows(WalletRechargeException.class, () -> {
            balanceService.recharge(ID, CREDIT_CARD_NUMBER, AMOUNT);
        });
    }

    @Test
    public void charge_whenConditionOK_thenChargeInWallet() {
        balanceService.charge(ID, AMOUNT);

        verify(walletRepository, times(1)).save(wallet);
        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(900));
    }

    @Test
    public void charge_whenNotEnoughBalance_thenThrowWalletChargeException() {
        wallet.setBalance(new BigDecimal(0));

        Assertions.assertThrows(WalletChargeException.class, () -> {
            balanceService.charge(ID, AMOUNT);
        });
    }
}