package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.ChargeRequest;
import com.playtomic.tests.wallet.dto.RechargeRequest;
import com.playtomic.tests.wallet.dto.WalletDTO;
import com.playtomic.tests.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.repository.JPAWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    private static final String WALLET_NOT_FOUND_EXCEPTION = "Wallet not found";
    private static final String WALLET_RECHARGE_EXCEPTION = "Recharge failed. Minimum amount rechargeable is 10.";
    private static final String WALLET_CHARGE_EXCEPTION = "Not enough credit in your wallet.";

    private static final Long id = 1L;
    private static final BigDecimal balance = new BigDecimal(1000);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JPAWalletRepository walletRepository;

    private WalletDTO walletDTO;

    private Wallet wallet;

    private RechargeRequest rechargeRequest;

    private RechargeRequest invalidRechargeRequest;

    private ChargeRequest chargeRequest;

    private ChargeRequest bigChargeRequest;

    private JacksonTester<WalletDTO> jsonWalletDTO;

    private JacksonTester<RechargeRequest> jsonRechargeRequest;

    private JacksonTester<ChargeRequest> jsonChargeRequest;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        walletDTO = WalletDTO.builder()
                .id(id)
                .balance(balance)
                .build();
        wallet = Wallet.builder()
                .id(id)
                .balance(balance)
                .build();
        rechargeRequest = RechargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(40))
                .creditCardNumber("1234")
                .build();
        invalidRechargeRequest = RechargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(5))
                .creditCardNumber("1234")
                .build();
        chargeRequest = ChargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(10))
                .build();
        bigChargeRequest = ChargeRequest.builder()
                .walletId(id)
                .amount(new BigDecimal(2000))
                .build();
    }

    @Test
    public void getWallet_whenWalletNotExists_thenReturnException() throws Exception {
        given(walletRepository.findById(id)).willThrow(new WalletNotFoundException());

        MockHttpServletResponse response = mvc.perform(get("/wallet/1").accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains(WALLET_NOT_FOUND_EXCEPTION);
    }

    @Test
    public void getWallet_whenWalletExists_thenReturnWallet() throws Exception {
        given(walletRepository.findById(id)).willReturn(Optional.of(wallet));

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
    public void createWallet_whenNewWallet_thenCreateWallet() throws Exception {

        MockHttpServletResponse response = mvc.perform(post("/wallet").contentType(MediaType.APPLICATION_JSON).content(jsonWalletDTO.write(walletDTO).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void recharge_whenValidRequest_thenRechargeWallet() throws Exception {
        given(walletRepository.findById(id)).willReturn(Optional.of(wallet));

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
        given(walletRepository.findById(id)).willReturn(Optional.of(wallet));

        MockHttpServletResponse response = mvc.perform(patch("/wallet/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRechargeRequest.write(invalidRechargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains(WALLET_RECHARGE_EXCEPTION);
    }

    @Test
    public void recharge_whenWalletNotExists_thenWalletNofFoundException() throws Exception {

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
        given(walletRepository.findById(id)).willReturn(Optional.of(wallet));

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
        given(walletRepository.findById(id)).willReturn(Optional.of(wallet));

        MockHttpServletResponse response = mvc.perform(patch("/wallet/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonChargeRequest.write(bigChargeRequest).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getContentAsString()).contains(WALLET_CHARGE_EXCEPTION);
    }
}