package com.aleksmaksprojects.controller;

import com.aleksmaksprojects.controller.pojo.WalletOperationRequest;
import com.aleksmaksprojects.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

     @PostMapping("wallet")
     public ResponseEntity<Void> performOperation(@RequestBody WalletOperationRequest request){
        walletService.performOperation(
                request.getWalletId(),
                request.getOperationType(),
                request.getAmount());
        return ResponseEntity.ok().build();
     }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<Double> getBalance(@PathVariable UUID walletId){
        double balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }
}
