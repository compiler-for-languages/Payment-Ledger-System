package com.infotact.project1.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionsTest {

    @Test
    void shouldCreateInsufficientBalanceException() {
        BusinessException exception = BusinessExceptions.insufficientBalance();

        assertEquals("Insufficient balance", exception.getMessage());
        assertEquals(400, exception.getStatus().value());
    }

    @Test
    void shouldCreateWalletNotFoundException() {
        BusinessException exception = BusinessExceptions.walletNotFound(99L);

        assertEquals("Wallet not found with id: 99", exception.getMessage());
        assertEquals(404, exception.getStatus().value());
    }
}
