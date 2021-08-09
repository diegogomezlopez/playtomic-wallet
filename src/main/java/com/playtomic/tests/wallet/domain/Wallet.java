package com.playtomic.tests.wallet.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public final class Wallet implements Serializable {

    @Id
    private Long id;
    private BigDecimal balance;
}
