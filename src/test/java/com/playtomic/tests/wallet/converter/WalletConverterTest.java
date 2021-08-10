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
public class WalletConverterTest {

    @InjectMocks
    private WalletConverter walletConverter;

    private Wallet wallet;
    private WalletDTO walletDTO;

    @BeforeEach
    public void init() {
        wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();

        walletDTO = WalletDTO.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(3500))
                .build();
    }

    @Test
    public void convertToDTO_whenWallet_thenReturnWalletDTO() {
        WalletDTO walletDTO = walletConverter.convertToDTO(wallet);
        assertEquals(wallet.getId(), walletDTO.getId());
        assertEquals(wallet.getBalance(), walletDTO.getBalance());
    }

    @Test
    public void convertToDTO_whenWalletNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> walletConverter.convertToDTO(null));
    }

    @Test
    public void convertToEntity_whenWalletDTO_thenReturnWallet() {
        Wallet wallet = walletConverter.convertToEntity(walletDTO);
        assertEquals(walletDTO.getId(), wallet.getId());
        assertEquals(walletDTO.getBalance(), wallet.getBalance());
    }

    @Test
    public void convertToEntity_whenWalletDTONUll_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> walletConverter.convertToEntity(null));
    }
}