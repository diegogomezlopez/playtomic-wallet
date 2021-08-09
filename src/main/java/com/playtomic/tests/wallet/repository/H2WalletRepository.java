package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class H2WalletRepository implements WalletRepository {

    private static final String TABLE_NAME = "wallets";
    private static final String ID = "id";
    private static final String BALANCE = "balance";

    private final Connection connection;

    public H2WalletRepository(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        try {
            Statement statement = connection.createStatement();
            String query = getFindByIdQuery(id);
            ResultSet result = statement.executeQuery(query);
            Wallet wallet = extractWalletFromResult(result);
            statement.close();
            return Optional.ofNullable(wallet);
        } catch (SQLException exception) {
            throw new WalletRepositoryException(exception.getLocalizedMessage());
        }
    }

    @Override
    public void save(final Wallet wallet) {
        try {
            Statement statement = connection.createStatement();
            String query = getCreateQuery(wallet);
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            throw new WalletRepositoryException(exception.getLocalizedMessage());
        }
    }

    private Wallet extractWalletFromResult(final ResultSet result) throws SQLException {
        if (result != null && result.next()) {
            return Wallet.builder()
            .id(result.getLong(ID))
            .balance(result.getBigDecimal(BALANCE))
            .build();
        }
        return null;
    }

    private String getFindByIdQuery(final Long id) {
        return "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
    }

    private String getCreateQuery(final Wallet wallet) {
        return "INSERT INTO " + TABLE_NAME + " VALUES " + "(" + wallet.getId() + "," + wallet.getBalance() + ")";
    }


}
