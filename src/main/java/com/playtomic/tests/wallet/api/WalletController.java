package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.dto.ChargeRequest;
import com.playtomic.tests.wallet.dto.RechargeRequest;
import com.playtomic.tests.wallet.dto.WalletDTO;
import com.playtomic.tests.wallet.service.BalanceService;
import com.playtomic.tests.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final BalanceService balanceService;

    public WalletController(final WalletService walletService, final BalanceService balanceService) {
        this.walletService = walletService;
        this.balanceService = balanceService;
    }

    @GetMapping("/{id}")
    public WalletDTO getWallet(final @PathVariable Long id) {
        return walletService.getWallet(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createWallet(final @RequestBody WalletDTO walletDTO) {
        walletService.createWallet(walletDTO);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/recharge")
    public void recharge(final @RequestBody RechargeRequest request) {
        balanceService.recharge(request.getWalletId(), request.getCreditCardNumber(), request.getAmount());
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/charge")
    public void charge(final @RequestBody ChargeRequest request) {
        balanceService.charge(request.getWalletId(), request.getAmount());
    }
}
