package com.infotact.project1.exception;

import org.springframework.http.HttpStatus;

public final class BusinessExceptions {

    private BusinessExceptions() {
    }

    public static BusinessException userNotFound(Long id) {
        return new BusinessException("User not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static BusinessException walletNotFound(Long id) {
        return new BusinessException("Wallet not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static BusinessException transactionNotFound(String reference) {
        return new BusinessException("Transaction not found with reference: " + reference, HttpStatus.NOT_FOUND);
    }

    public static BusinessException duplicateEmail(String email) {
        return new BusinessException("Email already exists: " + email, HttpStatus.CONFLICT);
    }

    public static BusinessException duplicateUsername(String username) {
        return new BusinessException("Username already exists: " + username, HttpStatus.CONFLICT);
    }

    public static BusinessException duplicateWallet(Long userId) {
        return new BusinessException("Wallet already exists for user id: " + userId, HttpStatus.CONFLICT);
    }

    public static BusinessException duplicateTransaction(String reference) {
        return new BusinessException("Duplicate transaction request: " + reference, HttpStatus.CONFLICT);
    }

    public static BusinessException invalidAmount() {
        return new BusinessException("Invalid amount. Amount must be greater than zero", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException("Insufficient balance", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException walletFrozen() {
        return new BusinessException("Wallet is frozen", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException walletBlocked() {
        return new BusinessException("Wallet is blocked", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException invalidTransfer() {
        return new BusinessException("Invalid transfer request", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException transactionFailed() {
        return new BusinessException("Transaction failed", HttpStatus.BAD_REQUEST);
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException unauthorized() {
        return new BusinessException("Unauthorized operation", HttpStatus.UNAUTHORIZED);
    }
}
