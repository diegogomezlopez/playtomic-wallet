package com.playtomic.tests.wallet.converter;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WalletToDTOConverterTest {

    @InjectMocks
    private WalletToDTOConverter walletToDTOConverter;

    private Wallet wallet;

    @BeforeEach
    public void init() {
        wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    public void convert_whenWallet_thenReturnWalletDTO() {
        WalletDTO walletDTO = walletToDTOConverter.convert(wallet);
        assertEquals(wallet.getId(), walletDTO.getId());
        assertEquals(wallet.getBalance(), walletDTO.getBalance());
    }

    @Test
    public void convert_whenWalletNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            walletToDTOConverter.convert(null);
        });
    }

}