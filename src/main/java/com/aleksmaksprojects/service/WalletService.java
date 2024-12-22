package com.aleksmaksprojects.service;

import com.aleksmaksprojects.exception.InsufficientFundsException;
import com.aleksmaksprojects.exception.WalletNotFoundException;
import com.aleksmaksprojects.model.Wallet;
import com.aleksmaksprojects.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void performOperation(UUID walletId, String operationType, double amount){
        Wallet wallet = entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE);
        if (wallet == null){
            throw new WalletNotFoundException("Wallet with id " + walletId + " not found.");
        }

        switch (operationType){
            case "DEPOSIT":
                wallet.setBalance(wallet.getBalance() + amount);
                break;
            case "WITHDRAW":
                if (wallet.getBalance() < amount){
                    throw new InsufficientFundsException("Insufficient funds for transaction.");
                }
                wallet.setBalance(wallet.getBalance() - amount);
                break;
            default:
                throw new IllegalArgumentException("Operation type must be DEPOSIT or WITHDRAW.");
        }
    }

    @Transactional
    public double getBalance(UUID walletId){
        Wallet wallet = entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_READ);
        if (wallet == null){
            throw new WalletNotFoundException("Wallet with id " + walletId + " not found.");
        }
        return wallet.getBalance();
    }
}
