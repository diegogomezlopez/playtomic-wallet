package com.playtomic.tests.wallet.converter;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;
import org.springframework.stereotype.Component;

@Component
public class WalletToDTOConverter {

    public WalletDTO convert(final Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }

}
