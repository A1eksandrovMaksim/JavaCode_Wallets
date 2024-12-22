package com.aleksmaksprojects.controller;

import com.aleksmaksprojects.controller.pojo.WalletOperationRequest;
import com.aleksmaksprojects.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletControllerTest {

    private WalletController walletController;
    private WalletService walletService;

    @BeforeEach
    void setUp(){
        walletService = mock(WalletService.class);
        walletController = new WalletController(walletService);
    }

    @Test
    void testPerformOperation(){
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType("DEPOSIT");
        request.setAmount(100.0);

        ResponseEntity<Void> responseEntity = walletController.performOperation(request);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ArgumentCaptor<UUID> walletIdCaptor  = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> operationTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);
        verify(walletService).performOperation(
                walletIdCaptor.capture(),
                operationTypeCaptor.capture(),
                amountCaptor.capture());
        assertEquals(request.getWalletId(), walletIdCaptor.getValue());
        assertEquals(request.getOperationType(), operationTypeCaptor.getValue());
        assertEquals(request.getAmount(), amountCaptor.getValue());
    }

    @Test
    void testGetBalance(){
        UUID walletId = UUID.randomUUID();
        double expectedBalance = 250.0;

        when(walletService.getBalance(walletId)).thenReturn(expectedBalance);
        ResponseEntity<Double> responseEntity = walletController.getBalance(walletId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedBalance, responseEntity.getBody());
        verify(walletService).getBalance(walletId);
    }


}
