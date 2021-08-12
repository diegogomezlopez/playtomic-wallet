package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class H2WalletRepositoryTest {

    private static final Long ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal(10);
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM wallets WHERE id = " + ID;
    private static final String CREATE_QUERY = "INSERT INTO wallets VALUES (" + ID + "," + BALANCE + ")";

    @InjectMocks
    private H2WalletRepository walletRepository;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    private Wallet wallet;

    @BeforeEach
    public void setup() throws SQLException {
        wallet = Wallet.builder()
                .id(ID)
                .balance(BALANCE)
                .build();
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    public void findById_whenId_thenReturnWallet() throws SQLException {
        when(statement.executeQuery(FIND_BY_ID_QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(ID);
        when(resultSet.getBigDecimal("balance")).thenReturn(BALANCE);

        Optional<Wallet> wallet = walletRepository.findById(ID);

        assertThat(wallet.get().getId()).isEqualTo(ID);
        assertThat(wallet.get().getBalance()).isEqualTo(BALANCE);
    }

    @Test
    public void findById_whenIdNotExist_thenNull() throws SQLException {
        when(statement.executeQuery(FIND_BY_ID_QUERY)).thenReturn(null);

        Optional<Wallet> wallet = walletRepository.findById(ID);

        assertThat(wallet).isEqualTo(Optional.empty());
    }

    @Test
    public void findById_whenSQLException_thenThrowWalletRepositoryException() throws SQLException {
        when(statement.executeQuery(FIND_BY_ID_QUERY)).thenThrow(new SQLException());

        Assertions.assertThrows(WalletRepositoryException.class, () -> {
            walletRepository.findById(ID);
        });
    }

    @Test
    public void save_whenOk_thenSaveWallet() throws SQLException {
        when(statement.executeUpdate(CREATE_QUERY)).thenReturn(1);

        walletRepository.save(wallet);

        verify(statement, times(1)).executeUpdate(CREATE_QUERY);
    }

    @Test
    public void save_whenSQLException_thenThrowWalletRepositoryException() throws SQLException {
        when(statement.executeUpdate(CREATE_QUERY)).thenThrow(new SQLException());

        Assertions.assertThrows(WalletRepositoryException.class, () -> {
            walletRepository.save(wallet);
        });
    }

}