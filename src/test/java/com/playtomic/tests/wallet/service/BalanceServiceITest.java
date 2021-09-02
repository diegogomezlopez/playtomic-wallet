package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.repository.JPAWalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BalanceServiceITest {

    private static final Long ID = 1L;
    private static final String CREDIT_CARD_NUMBER = "1234";
    private static final BigDecimal BALANCE = new BigDecimal(1000);
    private static final BigDecimal AMOUNT1 = new BigDecimal(10);
    private static final BigDecimal AMOUNT2 = new BigDecimal(20);
    private static final BigDecimal AMOUNT3 = new BigDecimal(30);
    private static final BigDecimal AMOUNT4 = new BigDecimal(500);

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private JPAWalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    public void setup() {
        wallet = Wallet.builder()
                .id(ID)
                .balance(BALANCE)
                .build();
    }

    @AfterEach
    public void cleanUp() {
        walletRepository.deleteAll();
    }

    @Test
    public void concurrency_whenMultipleCharges_thenResultIsExpected() throws InterruptedException {
        walletRepository.save(wallet);

        Set<BigDecimal> amounts = new HashSet<>();
        amounts.add(AMOUNT1);
        amounts.add(AMOUNT2);
        amounts.add(AMOUNT3);

        final ExecutorService executor = Executors.newFixedThreadPool(amounts.size());

        amounts.forEach(amount -> executor.execute(() -> balanceService.charge(ID, amount)));

        executor.shutdown();
        if (executor.awaitTermination(1, TimeUnit.MINUTES)){
            Wallet result = walletRepository.findById(ID).orElseThrow(WalletNotFoundException::new);
            assertThat(wallet.getBalance().subtract(AMOUNT1).subtract(AMOUNT2).subtract(AMOUNT3)).isEqualTo(result.getBalance());
        }
    }

    @Test
    public void concurrency_whenChargesAboveBalance_thenResultNeverNegative() throws InterruptedException {
        walletRepository.save(wallet);

        List<BigDecimal> amounts = new ArrayList<>();
        amounts.add(AMOUNT4);
        amounts.add(AMOUNT4);
        amounts.add(AMOUNT4);

        final ExecutorService executor = Executors.newFixedThreadPool(amounts.size());

        amounts.forEach(amount -> executor.execute(() -> balanceService.charge(ID, amount)));

        executor.shutdown();
        if (executor.awaitTermination(1, TimeUnit.MINUTES)){
            Wallet result = walletRepository.findById(ID).orElseThrow(WalletNotFoundException::new);
            assertThat(BigDecimal.ZERO).isEqualTo(result.getBalance());
        }
    }

    @Test
    public void concurrency_whenMultipleRecharges_thenResultIsExpected() throws InterruptedException {
        walletRepository.save(wallet);

        Set<BigDecimal> amounts = new HashSet<>();
        amounts.add(AMOUNT1);
        amounts.add(AMOUNT2);
        amounts.add(AMOUNT3);

        final ExecutorService executor = Executors.newFixedThreadPool(amounts.size());

        amounts.forEach(amount -> executor.execute(() -> balanceService.recharge(ID, CREDIT_CARD_NUMBER, amount)));

        executor.shutdown();
        if (executor.awaitTermination(1, TimeUnit.MINUTES)){
            Wallet result = walletRepository.findById(ID).orElseThrow(WalletNotFoundException::new);
            assertThat(wallet.getBalance().add(AMOUNT1).add(AMOUNT2).add(AMOUNT3)).isEqualTo(result.getBalance());
        }
    }
}
