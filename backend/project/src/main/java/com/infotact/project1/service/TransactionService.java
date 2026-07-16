package com.infotact.project1.service;

import com.infotact.project1.dto.request.AmountRequestDTO;
import com.infotact.project1.dto.request.TransferRequestDTO;
import com.infotact.project1.enums.*;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.LedgerEntry;
import com.infotact.project1.model.Transaction;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.LedgerEntryRepository;
import com.infotact.project1.repository.TransactionRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AuditLogService auditLogService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    public List<TransactionResponseDTO> getTransactions(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        return transactionRepository.findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet)
                .stream()
                .map(this::mapTransaction)
                .toList();
    }

    public TransactionResponseDTO getTransactionByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> BusinessExceptions.transactionNotFound(referenceNumber));
        return mapTransaction(transaction);
    }

    public List<TransactionResponseDTO> filterMyTransactions(Long userId,
                                                             TransactionType type,
                                                             TransactionStatus status,
                                                             LocalDate from,
                                                             LocalDate to) {
        Wallet wallet = getWalletByUserId(userId);
        LocalDateTime fromDateTime = from == null ? LocalDate.MIN.atStartOfDay() : from.atStartOfDay();
        LocalDateTime toDateTime = to == null ? LocalDate.MAX.atTime(LocalTime.MAX) : to.atTime(LocalTime.MAX);

        return transactionRepository.findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet)
                .stream()
                .filter(transaction -> type == null || transaction.getType() == type)
                .filter(transaction -> status == null || transaction.getStatus() == status)
                .filter(transaction -> !transaction.getCreatedAt().isBefore(fromDateTime))
                .filter(transaction -> !transaction.getCreatedAt().isAfter(toDateTime))
                .map(this::mapTransaction)
                .toList();
    }

    @Transactional
    public TransactionResponseDTO deposit(Long userId, AmountRequestDTO requestDTO) {
        // Deposit flow validates amount and wallet state, then updates balance with lock-safe mutation.
        validateAmount(requestDTO.getAmount());
        Wallet wallet = getWalletByUserId(userId);
        validateWalletForCredit(wallet);

        RLock lock = redissonClient.getLock("wallet:" + wallet.getId());
        lock.lock();
        try {
            Transaction transaction = Transaction.builder()
                    .referenceNumber(reference())
                    .type(TransactionType.DEPOSIT)
                    .status(TransactionStatus.SUCCESS)
                    .amount(requestDTO.getAmount())
                    .receiverWallet(wallet)
                    .description(safeDescription(requestDTO.getDescription(), "Deposit"))
                    .createdAt(LocalDateTime.now())
                    .build();

            wallet.setBalance(wallet.getBalance().add(requestDTO.getAmount()));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            Transaction saved = transactionRepository.save(transaction);
            ledgerEntryRepository.save(LedgerEntry.builder()
                    .transaction(saved)
                    .wallet(wallet)
                    .entryType(LedgerEntryType.CREDIT)
                    .amount(requestDTO.getAmount())
                    .narration("Deposit successful")
                    .createdAt(LocalDateTime.now())
                    .build());

            auditLogService.createAuditLog(AuditActionType.DEPOSIT_SUCCESS, userId.toString(), "Transaction",
                    "Deposit successful for wallet: " + wallet.getId());

            return mapTransaction(saved);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public TransactionResponseDTO withdraw(Long userId, AmountRequestDTO requestDTO) {
        // Withdraw flow records failed attempts and creates ledger only for successful debit.
        validateAmount(requestDTO.getAmount());
        Wallet wallet = getWalletByUserId(userId);
        validateWalletForDebit(wallet);

        RLock lock = redissonClient.getLock("wallet:" + wallet.getId());
        lock.lock();
        try {
            if (wallet.getBalance().compareTo(requestDTO.getAmount()) < 0) {
                Transaction failed = saveFailedTransaction(TransactionType.WITHDRAWAL, requestDTO.getAmount(), wallet, null,
                        "Failed withdraw due to insufficient balance");
                auditLogService.createAuditLog(AuditActionType.TRANSACTION_FAILED, userId.toString(), "Transaction",
                        "Withdraw failed due to insufficient balance");
                return mapTransaction(failed);
            }

            wallet.setBalance(wallet.getBalance().subtract(requestDTO.getAmount()));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            Transaction saved = transactionRepository.save(Transaction.builder()
                    .referenceNumber(reference())
                    .type(TransactionType.WITHDRAWAL)
                    .status(TransactionStatus.SUCCESS)
                    .amount(requestDTO.getAmount())
                    .senderWallet(wallet)
                    .description(safeDescription(requestDTO.getDescription(), "Withdraw"))
                    .createdAt(LocalDateTime.now())
                    .build());

            ledgerEntryRepository.save(LedgerEntry.builder()
                    .transaction(saved)
                    .wallet(wallet)
                    .entryType(LedgerEntryType.DEBIT)
                    .amount(requestDTO.getAmount())
                    .narration("Withdraw successful")
                    .createdAt(LocalDateTime.now())
                    .build());

            auditLogService.createAuditLog(AuditActionType.WITHDRAW_SUCCESS, userId.toString(), "Transaction",
                    "Withdraw successful for wallet: " + wallet.getId());
            return mapTransaction(saved);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public TransactionResponseDTO transfer(Long senderUserId, TransferRequestDTO requestDTO, String idempotencyKey) {
        // Transfer flow uses idempotency + deterministic dual-lock ordering to avoid duplicates and race conditions.
        validateAmount(requestDTO.getAmount());
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw BusinessExceptions.invalidTransfer();
        }
        if (senderUserId.equals(requestDTO.getReceiverUserId())) {
            throw BusinessExceptions.invalidTransfer();
        }

        Wallet senderWallet = getWalletByUserId(senderUserId);
        Wallet receiverWallet = getWalletByUserId(requestDTO.getReceiverUserId());
        validateWalletForDebit(senderWallet);
        validateWalletForCredit(receiverWallet);

        String idempotencyCacheKey = "idempotency:" + idempotencyKey;
        String existingReference = stringRedisTemplate.opsForValue().get(idempotencyCacheKey);
        if (existingReference != null) {
            auditLogService.createAuditLog(AuditActionType.DUPLICATE_REQUEST_REJECTED, senderUserId.toString(), "Transaction",
                    "Duplicate transfer request rejected for idempotency key: " + idempotencyKey);
            Transaction existingTransaction = transactionRepository.findByReferenceNumber(existingReference)
                    .orElseThrow(() -> BusinessExceptions.transactionNotFound(existingReference));
            return mapTransaction(existingTransaction);
        }

        Wallet firstLockWallet = senderWallet.getId() < receiverWallet.getId() ? senderWallet : receiverWallet;
        Wallet secondLockWallet = senderWallet.getId() < receiverWallet.getId() ? receiverWallet : senderWallet;

        RLock firstLock = redissonClient.getLock("wallet:" + firstLockWallet.getId());
        RLock secondLock = redissonClient.getLock("wallet:" + secondLockWallet.getId());

        try {
            boolean firstLockAcquired = firstLock.tryLock(3, 10, TimeUnit.SECONDS);
            boolean secondLockAcquired = secondLock.tryLock(3, 10, TimeUnit.SECONDS);

            if (!firstLockAcquired || !secondLockAcquired) {
                throw BusinessExceptions.invalidTransfer();
            }

            if (senderWallet.getBalance().compareTo(requestDTO.getAmount()) < 0) {
                Transaction failed = saveFailedTransaction(TransactionType.TRANSFER, requestDTO.getAmount(), senderWallet, receiverWallet,
                        "Failed transfer due to insufficient balance");
                auditLogService.createAuditLog(AuditActionType.TRANSACTION_FAILED, senderUserId.toString(), "Transaction",
                        "Transfer failed due to insufficient balance");
                return mapTransaction(failed);
            }

            senderWallet.setBalance(senderWallet.getBalance().subtract(requestDTO.getAmount()));
            receiverWallet.setBalance(receiverWallet.getBalance().add(requestDTO.getAmount()));
            senderWallet.setUpdatedAt(LocalDateTime.now());
            receiverWallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(senderWallet);
            walletRepository.save(receiverWallet);

            Transaction saved = transactionRepository.save(Transaction.builder()
                    .referenceNumber(reference())
                    .type(TransactionType.TRANSFER)
                    .status(TransactionStatus.SUCCESS)
                    .amount(requestDTO.getAmount())
                    .senderWallet(senderWallet)
                    .receiverWallet(receiverWallet)
                    .description(requestDTO.getDescription())
                    .createdAt(LocalDateTime.now())
                    .build());

            ledgerEntryRepository.save(LedgerEntry.builder()
                    .transaction(saved)
                    .wallet(senderWallet)
                    .entryType(LedgerEntryType.DEBIT)
                    .amount(requestDTO.getAmount())
                    .narration("Transfer sent")
                    .createdAt(LocalDateTime.now())
                    .build());

            ledgerEntryRepository.save(LedgerEntry.builder()
                    .transaction(saved)
                    .wallet(receiverWallet)
                    .entryType(LedgerEntryType.CREDIT)
                    .amount(requestDTO.getAmount())
                    .narration("Transfer received")
                    .createdAt(LocalDateTime.now())
                    .build());

            stringRedisTemplate.opsForValue().set(idempotencyCacheKey, saved.getReferenceNumber(), Duration.ofHours(24));

            auditLogService.createAuditLog(AuditActionType.TRANSFER_SUCCESS, senderUserId.toString(), "Transaction",
                    "Transfer successful from wallet " + senderWallet.getId() + " to " + receiverWallet.getId());

            return mapTransaction(saved);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw BusinessExceptions.invalidTransfer();
        } finally {
            if (secondLock.isHeldByCurrentThread()) {
                secondLock.unlock();
            }
            if (firstLock.isHeldByCurrentThread()) {
                firstLock.unlock();
            }
        }
    }

    private Transaction saveFailedTransaction(TransactionType type,
                                              BigDecimal amount,
                                              Wallet sender,
                                              Wallet receiver,
                                              String description) {
        return transactionRepository.save(Transaction.builder()
                .referenceNumber(reference())
                .type(type)
                .status(TransactionStatus.FAILED)
                .amount(amount)
                .senderWallet(sender)
                .receiverWallet(receiver)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Wallet getWalletByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessExceptions.userNotFound(userId));
        return walletRepository.findByUser(user)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(userId));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessExceptions.invalidAmount();
        }
    }

    private void validateWalletForCredit(Wallet wallet) {
        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw BusinessExceptions.walletBlocked();
        }
    }

    private void validateWalletForDebit(Wallet wallet) {
        if (wallet.getStatus() == WalletStatus.FROZEN) {
            throw BusinessExceptions.walletFrozen();
        }
        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw BusinessExceptions.walletBlocked();
        }
    }

    private String reference() {
        return "TXN-" + UUID.randomUUID();
    }

    private String safeDescription(String raw, String fallback) {
        return raw == null || raw.isBlank() ? fallback : raw;
    }

    private TransactionResponseDTO mapTransaction(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .senderWalletId(transaction.getSenderWallet() != null ? transaction.getSenderWallet().getId() : null)
                .receiverWalletId(transaction.getReceiverWallet() != null ? transaction.getReceiverWallet().getId() : null)
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
