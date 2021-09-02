package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class JPAWalletRepositoryITest {

    private static final Long ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal(1000);

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
}