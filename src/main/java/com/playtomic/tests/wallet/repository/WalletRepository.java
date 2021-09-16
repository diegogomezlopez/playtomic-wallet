package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
@Transactional
public interface WalletRepository extends CrudRepository<Wallet, Long> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findById(Long id);
}
