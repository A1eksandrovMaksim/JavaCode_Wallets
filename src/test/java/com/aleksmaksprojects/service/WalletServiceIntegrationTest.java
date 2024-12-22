package com.aleksmaksprojects.service;

import com.aleksmaksprojects.exception.InsufficientFundsException;
import com.aleksmaksprojects.exception.WalletNotFoundException;
import com.aleksmaksprojects.model.Wallet;
import com.aleksmaksprojects.repository.WalletRepository;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class WalletServiceIntegrationTest {

    private final WalletService walletService;
    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceIntegrationTest(WalletService walletService, WalletRepository walletRepository) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    @BeforeEach
    void setUp(){
        walletRepository.deleteAll();
    }

    @Test
    void testPerformOperationDeposit(){
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200.0);
        walletRepository.save(wallet);

        walletService.performOperation(walletId, "DEPOSIT", 100.0);

        Wallet updatedWallet = walletRepository.findById(walletId).orElseThrow();
        assertEquals(300.0, updatedWallet.getBalance());
    }

    @Test
    void testPerformOperationWithdraw(){
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200);
        walletRepository.save(wallet);

        walletService.performOperation(walletId, "WITHDRAW", 100.0);

        Wallet updatedWallet = walletRepository.findById(walletId).orElseThrow();
        assertEquals(100.0, updatedWallet.getBalance());
    }

    @Test
    void testPerformOperationWithdrawInsufficientFunds(){
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(50.0);
        walletRepository.save(wallet);

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.performOperation(walletId, "WITHDRAW", 100.0);
        });

        Wallet updatedWallet = walletRepository.findById(walletId).orElseThrow();
        assertEquals(50.0, updatedWallet.getBalance());
    }

    @Test
    void testGetBalance(){
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200.0);
        walletRepository.save(wallet);

        double balance = walletService.getBalance(walletId);
        assertEquals(200.0, balance);
    }

    @Test
    void testGetBalanceWalletNotFound(){
        UUID walletId = UUID.randomUUID();

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(walletId);
        });
    }
}
