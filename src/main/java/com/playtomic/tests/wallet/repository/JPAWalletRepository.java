package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.WalletRepositoryException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

@Repository
public class JPAWalletRepository implements WalletRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public JPAWalletRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        try {
            Wallet wallet = entityManager.find(Wallet.class, id, LockModeType.PESSIMISTIC_WRITE);
            return Optional.ofNullable(wallet);
        } catch (Exception exception) {
            throw new WalletRepositoryException(exception.getLocalizedMessage());
        }
    }

    @Override
    public void save(final Wallet wallet) {
        try {
            entityManager.persist(wallet);
        } catch (Exception exception) {
            throw new WalletRepositoryException(exception.getLocalizedMessage());
        }
    }

    @Override
    public void deleteAll() {
        try {
            Query deleteAllQuery = entityManager.createNativeQuery("TRUNCATE TABLE WALLET");
            deleteAllQuery.executeUpdate();
        } catch (Exception exception) {
            throw new WalletRepositoryException(exception.getLocalizedMessage());
        }
    }
}
