package com.playtomic.tests.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeRequest {

    private Long walletId;
    private String creditCardNumber;
    private BigDecimal amount;
}
