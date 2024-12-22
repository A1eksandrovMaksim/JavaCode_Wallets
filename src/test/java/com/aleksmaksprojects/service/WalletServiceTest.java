package com.aleksmaksprojects.service;

import com.aleksmaksprojects.exception.InsufficientFundsException;
import com.aleksmaksprojects.exception.WalletNotFoundException;
import com.aleksmaksprojects.model.Wallet;
import com.aleksmaksprojects.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
public class WalletServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WalletService walletService;

    @Test
    void testPerformOperationDeposit() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200.0);

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        walletService.performOperation(walletId, "DEPOSIT", 100.0);
        assertEquals(300.0, wallet.getBalance());
    }

    @Test
    void testPerformOperationWithdraw() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200.0);

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        walletService.performOperation(walletId, "WITHDRAW", 100.0);
        assertEquals(100.0, wallet.getBalance());
    }

    @Test
    void testPerformOperationWithdrawInsufficientFunds() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(50.0);

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.performOperation(walletId, "WITHDRAW", 100.0);
        });
    }

    @Test
    void testPerformOperationWalletNotFound() {
        UUID walletId = UUID.randomUUID();

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.performOperation(walletId, "WITHDRAW", 100.0);
        });
    }

    @Test
    void testGetBalance() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(200.0);

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_READ)).thenReturn(wallet);

        double balance = walletService.getBalance(walletId);
        assertEquals(200.0, balance);
    }

    @Test
    void testGetBalanceWalletNotFound() {
        UUID walletId = UUID.randomUUID();

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_READ)).thenReturn(null);

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(walletId);
        });
    }
}