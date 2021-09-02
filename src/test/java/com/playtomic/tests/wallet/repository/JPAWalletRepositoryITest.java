package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import com.playtomic.tests.wallet.service.BalanceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class JPAWalletRepositoryITest {

    private static final String CREDIT_CARD_NUMBER = "1234";

    private static final Long ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal(1000);
    private static final BigDecimal AMOUNT1 = new BigDecimal(10);
    private static final BigDecimal AMOUNT2 = new BigDecimal(20);
    private static final BigDecimal AMOUNT3 = new BigDecimal(30);
    private static final BigDecimal AMOUNT4 = new BigDecimal(500);

    @Autowired
    private JPAWalletRepository walletRepository;

    @Autowired
    private BalanceService balanceService;

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
    public void findById_whenWalletExists_thenReturnWallet() {
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findById(ID);

        assertTrue(result.isPresent());
        assertThat(wallet.getId()).isEqualTo(result.get().getId());
        assertThat(wallet.getBalance()).isEqualTo(result.get().getBalance());
    }

    @Test
    public void findById_whenWalletNotExists_thenReturnNull() {
        Optional<Wallet> result = walletRepository.findById(ID);

        assertFalse(result.isPresent());
        assertThat(Optional.empty()).isEqualTo(result);
    }

    @Test
    public void findById_whenException_thenThrowWalletRepositoryException() {
        Assertions.assertThrows(WalletRepositoryException.class, () -> walletRepository.findById(null));
    }

    @Test
    public void save_whenOk_thenCreateWallet() {
        walletRepository.save(wallet);

        assertThat(Optional.of(wallet)).isEqualTo(walletRepository.findById(ID));
    }

    @Test
    public void save_whenIdDuplicated_thenThrowDataIntegrityViolationException() {
        walletRepository.save(wallet);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> walletRepository.save(wallet));
    }

    @Test
    public void save_whenNull_thenThrowWalletRepositoryException() {
        Assertions.assertThrows(WalletRepositoryException.class, () -> walletRepository.save(null));
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