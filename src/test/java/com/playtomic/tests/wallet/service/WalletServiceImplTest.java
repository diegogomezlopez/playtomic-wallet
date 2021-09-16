package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.converter.WalletConverter;
import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDTO;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.exception.WalletServiceException;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    private static final Long ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal(100);

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletConverter walletConverter;

    private Wallet wallet;

    private WalletDTO walletDTO;

    @BeforeEach
    public void setup() {
        wallet = Wallet.builder()
                .id(ID)
                .balance(BALANCE)
                .build();
        walletDTO = WalletDTO.builder()
                .id(ID)
                .balance(BALANCE)
                .build();
    }

    @Test
    public void findWalletById_whenWalletExists_thenReturnWallet() {
        when(walletRepository.findById(ID)).thenReturn(Optional.of(wallet));

        Wallet wallet = walletService.findWalletById(ID);

        assertThat(wallet.getId()).isEqualTo(ID);
        assertThat(wallet.getBalance()).isEqualTo(BALANCE);
    }

    @Test
    public void findWalletById_whenWalletIdNull_thenThrowWalletServiceException() {
        Assertions.assertThrows(WalletServiceException.class, () -> walletService.findWalletById(null));
    }

    @Test
    public void findWalletById_whenWalletNotExists_thenThrowWalletNotFoundException() {
        Assertions.assertThrows(WalletNotFoundException.class, () -> walletService.findWalletById(ID));
    }

    @Test
    public void createWallet_whenWalletDTO_thenCreateWallet() {
        when(walletConverter.convertToEntity(walletDTO)).thenReturn(wallet);

        walletService.createWallet(walletDTO);

        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    public void getWallet_whenWalletExists_thenReturnWalletDTO() {
        when(walletRepository.findById(ID)).thenReturn(Optional.of(wallet));

        walletService.getWallet(ID);

        verify(walletConverter, times(1)).convertToDTO(wallet);
    }
}