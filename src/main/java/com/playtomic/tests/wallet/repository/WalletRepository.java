package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface WalletRepository extends Repository<Wallet, Long> {
    Optional<Wallet> findById(final Long id);

    void save(final Wallet wallet);

    void deleteAll();
}
