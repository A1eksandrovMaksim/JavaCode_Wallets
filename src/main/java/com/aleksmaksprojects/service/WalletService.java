package com.aleksmaksprojects.service;

import com.aleksmaksprojects.exception.InsufficientFundsException;
import com.aleksmaksprojects.exception.WalletNotFoundException;
import com.aleksmaksprojects.model.Wallet;
import com.aleksmaksprojects.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void performOperation(UUID walletId, String operationType, double amount){
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        switch (operationType){
            case "DEPOSIT":
                wallet.setBalance(wallet.getBalance() + amount);
                break;
            case "WITHDRAW":
                if (wallet.getBalance() < amount){
                    throw new InsufficientFundsException("Insufficient funds");
                }
                wallet.setBalance(wallet.getBalance() - amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation type");
        }

        walletRepository.save(wallet);
    }

    public double getBalance(UUID walletId){
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"))
                .getBalance();
    }
}
