package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.converter.WalletConverter;
import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.exception.WalletServiceException;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletConverter walletConverter;

    public WalletServiceImpl(final WalletRepository repository, final WalletConverter walletConverter) {
        this.walletRepository = repository;
        this.walletConverter = walletConverter;
    }

    @Override
    public Wallet findWalletById(final Long id) {
        if (id == null) {
            throw new WalletServiceException("Searching for wallet with id: null. Id cannot be null.");
        }
        return walletRepository.findById(id).orElseThrow(WalletNotFoundException::new);
    }

    @Override
    public void createWallet(final WalletDTO walletDTO) {
        Wallet wallet = walletConverter.convertToEntity(walletDTO);
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public WalletDTO getWallet(final Long id) {
        Wallet wallet = findWalletById(id);
        return walletConverter.convertToDTO(wallet);
    }
}
