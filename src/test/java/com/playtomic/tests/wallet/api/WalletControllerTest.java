package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.dto.ChargeRequest;
import com.playtomic.tests.wallet.dto.RechargeRequest;
import com.playtomic.tests.wallet.dto.WalletDTO;
import com.playtomic.tests.wallet.exception.AppExceptionHandler;
import com.playtomic.tests.wallet.exception.WalletChargeException;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.exception.WalletRechargeException;
import com.playtomic.tests.wallet.service.BalanceService;
import com.playtomic.tests.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    private static final String WALLET_NOT_FOUND_EXCEPTION = "Wallet not found";
    private static final String WALLET_RECHARGE_EXCEPTION = "Recharge failed. Minimum amount rechargeable is 10.";
    private static final String WALLET_CHARGE_EXCEPTION = "Not enough credit in your wallet.";

    private static final Long id = 1L;
    private static final BigDecimal balance = new BigDecimal(1000);

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @Mock
    private BalanceService balanceService;

    private MockMvc mvc;

    private WalletDTO walletDTO;

    private RechargeRequest rechargeRequest;

    private ChargeRequest chargeRequest;

    private JacksonTester<WalletDTO> jsonWalletDTO;

    private JacksonTester<RechargeRequest> jsonRechargeRequest;

    private JacksonTester<ChargeRequest> jsonChargeRequest;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new AppExceptionHandler())
                .build();
        walletDTO = WalletDTO.builder()
                .id(id)
                .balance(balance)
                .build();
        rechargeRequest = RechargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(40))
                .creditCardNumber("1234")
                .build();
        chargeRequest = ChargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(10))
                .build();
    }

    @Test
    public void getWallet_whenWalletExists_thenReturnWallet() throws Exception {
        when(walletService.getWallet(id)).thenReturn(walletDTO);

        MockHttpServletResponse response = mvc.perform(get("/wallet/1").accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonWalletDTO.write(WalletDTO.builder()
                        .id(id)
                        .balance(balance)
                        .build()).getJson()
        );
    }

    @Test
    public void getWallet_whenWalletNotExists_thenReturnException() throws Exception {
        when(walletService.getWallet(id)).thenThrow(new WalletNotFoundException());

        MockHttpServletResponse response = mvc.perform(get("/wallet/1").accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains(WALLET_NOT_FOUND_EXCEPTION);
    }

    @Test
    public void createWallet_whenNewWallet_thenCreateWallet() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/wallet").contentType(MediaType.APPLICATION_JSON).content(jsonWalletDTO.write(walletDTO).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void recharge_whenValidRequest_thenRechargeWallet() throws Exception {
        MockHttpServletResponse response = mvc.perform(patch("/wallet/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRechargeRequest.write(rechargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void recharge_whenMinimumAMountNotReached_thenRechargeException() throws Exception {
        doThrow(new WalletRechargeException(WALLET_RECHARGE_EXCEPTION)).when(balanceService).recharge(rechargeRequest.getWalletId(), rechargeRequest.getCreditCardNumber(), rechargeRequest.getAmount());

        MockHttpServletResponse response = mvc.perform(patch("/wallet/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRechargeRequest.write(rechargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Recharge failed. Minimum amount rechargeable is 10.");
    }

    @Test
    public void recharge_whenWalletNotExists_thenWalletNofFoundException() throws Exception {
        doThrow(new WalletNotFoundException()).when(balanceService).recharge(rechargeRequest.getWalletId(), rechargeRequest.getCreditCardNumber(), rechargeRequest.getAmount());

        MockHttpServletResponse response = mvc.perform(patch("/wallet/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRechargeRequest.write(rechargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains(WALLET_NOT_FOUND_EXCEPTION);
    }

    @Test
    public void charge_whenWalletHasEnoughBalance_thenChargeAmountToWallet() throws Exception {

        MockHttpServletResponse response = mvc.perform(patch("/wallet/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonChargeRequest.write(chargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void charge_whenWalletHasNotEnoughBalance_thenWalletChargeException() throws Exception {
        doThrow(new WalletChargeException(WALLET_CHARGE_EXCEPTION)).when(balanceService).charge(chargeRequest.getWalletId(), chargeRequest.getAmount());

        MockHttpServletResponse response = mvc.perform(patch("/wallet/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonChargeRequest.write(chargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getContentAsString()).contains(WALLET_CHARGE_EXCEPTION);
    }
}