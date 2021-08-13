package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.config.H2ConfigProperties;
import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class H2WalletRepositoryITest {

    private static final Long ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal(1000);
    private static final String CLEAN_UP_QUERY = "TRUNCATE TABLE wallets";

    @Autowired
    private H2WalletRepository walletRepository;

    @Autowired
    private Connection connection;

    @Autowired
    private H2ConfigProperties properties;

    private Wallet wallet;

    @BeforeEach
    public void setup() {
        wallet = Wallet.builder()
                .id(ID)
                .balance(BALANCE)
                .build();
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        if (connection.isClosed()) {
            connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
        }
        Statement statement = connection.createStatement();
        statement.execute(CLEAN_UP_QUERY);
        statement.close();
    }

    @Test
    public void findById_whenWalletExists_thenReturnWallet() {
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findById(ID);

        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(wallet.getId());
        assertThat(result.get().getBalance()).isEqualTo(wallet.getBalance());
    }

    @Test
    public void findById_whenWalletNotExists_thenReturnNull() {
        Optional<Wallet> result = walletRepository.findById(ID);

        assertFalse(result.isPresent());
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void findById_whenSQLException_thenThrowWalletRepositoryException() throws SQLException {
        connection.close();

        Assertions.assertThrows(WalletRepositoryException.class, () -> walletRepository.findById(null));
    }

    @Test
    public void save_whenOk_thenCreateWallet() {
        walletRepository.save(wallet);

        assertThat(walletRepository.findById(ID)).isEqualTo(Optional.of(wallet));
    }

    @Test
    public void save_whenIdDuplicated_thenThrowWalletRepositoryException() {
        walletRepository.save(wallet);

        Assertions.assertThrows(WalletRepositoryException.class, () -> walletRepository.save(wallet));
    }
}