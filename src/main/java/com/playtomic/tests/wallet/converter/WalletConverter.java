package com.playtomic.tests.wallet.converter;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;
import org.springframework.stereotype.Component;

@Component
public class WalletConverter {

    public WalletDTO convertToDTO(final Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }

    public Wallet convertToEntity(final WalletDTO walletDTO) {
        return Wallet.builder()
                .id(walletDTO.getId())
                .balance(walletDTO.getBalance())
                .build();
    }

}
