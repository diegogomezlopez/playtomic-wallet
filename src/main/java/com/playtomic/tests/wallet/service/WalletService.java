package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;

public interface WalletService {

    Wallet findWalletById(final Long id);

    void createWallet(final WalletDTO walletDTO);

    WalletDTO getWallet(final Long id);
}
