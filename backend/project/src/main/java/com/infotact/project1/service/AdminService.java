package com.infotact.project1.service;

import com.infotact.project1.dto.response.AuditLogResponseDTO;
import com.infotact.project1.dto.response.DashboardStatsResponseDTO;
import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.dto.response.TransactionSummaryResponseDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.dto.response.WalletResponseDTO;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.model.AuditLog;
import com.infotact.project1.model.Transaction;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.AuditLogRepository;
import com.infotact.project1.repository.TransactionRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardStatsResponseDTO getDashboardStats() {
        // Dashboard stats consolidate daily and system-wide counters for admin operational visibility.
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalBalance = walletRepository.findAll().stream()
                .map(wallet -> wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStatsResponseDTO.builder()
                .totalUsers(userRepository.count())
                .totalWallets(walletRepository.count())
                .totalBalance(totalBalance)
                .todayDeposits(transactionRepository.countByTypeAndCreatedAtBetween(TransactionType.DEPOSIT, start, end))
                .todayWithdrawals(transactionRepository.countByTypeAndCreatedAtBetween(TransactionType.WITHDRAWAL, start, end))
                .todayTransfers(transactionRepository.countByTypeAndCreatedAtBetween(TransactionType.TRANSFER, start, end))
                .failedTransactions(transactionRepository.countByStatusAndCreatedAtBetween(TransactionStatus.FAILED, start, end))
                .successfulTransactions(transactionRepository.countByStatusAndCreatedAtBetween(TransactionStatus.SUCCESS, start, end))
                .frozenWallets(walletRepository.countByStatus(WalletStatus.FROZEN))
                .blockedWallets(walletRepository.countByStatus(WalletStatus.BLOCKED))
                .activeWallets(walletRepository.countByStatus(WalletStatus.ACTIVE))
                .build();
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().getName())
                        .build())
                .toList();
    }

    public List<WalletResponseDTO> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(wallet -> WalletResponseDTO.builder()
                        .id(wallet.getId())
                        .userId(wallet.getUser().getId())
                        .balance(wallet.getBalance())
                        .status(wallet.getStatus())
                        .createdAt(wallet.getCreatedAt())
                        .updatedAt(wallet.getUpdatedAt())
                        .build())
                .toList();
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapTransaction)
                .toList();
    }

    public List<TransactionResponseDTO> getFailedTransactions() {
        return transactionRepository.findByStatusOrderByCreatedAtDesc(TransactionStatus.FAILED).stream()
                .map(this::mapTransaction)
                .toList();
    }

    public List<AuditLogResponseDTO> getAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapAudit)
                .toList();
    }

    public List<UserResponseDTO> getUsersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pagedUsers = userRepository.findAll(pageable);
        return pagedUsers.getContent().stream().map(this::mapUser).toList();
    }

    public List<WalletResponseDTO> getWalletsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Wallet> pagedWallets = walletRepository.findAll(pageable);
        return pagedWallets.getContent().stream().map(this::mapWallet).toList();
    }

    public List<TransactionResponseDTO> getTransactionsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> pagedTransactions = transactionRepository.findAll(pageable);
        return pagedTransactions.getContent().stream().map(this::mapTransaction).toList();
    }

    public List<UserResponseDTO> searchUsers(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();

        return userRepository.findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        safeKeyword,
                        safeKeyword,
                        safeKeyword
                )
                .stream()
                .map(this::mapUser)
                .toList();
    }

    public List<TransactionResponseDTO> searchTransactions(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();

        return transactionRepository.findByReferenceNumberContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
                        safeKeyword,
                        safeKeyword
                )
                .stream()
                .map(this::mapTransaction)
                .toList();
    }

    public List<TransactionResponseDTO> filterTransactions(TransactionType type,
                                                           TransactionStatus status,
                                                           LocalDate from,
                                                           LocalDate to) {
        // Filtering is layered in service so optional parameters remain flexible without complex repository branching.
        LocalDateTime fromDateTime = from == null ? LocalDate.MIN.atStartOfDay() : from.atStartOfDay();
        LocalDateTime toDateTime = to == null ? LocalDate.MAX.atTime(LocalTime.MAX) : to.atTime(LocalTime.MAX);

        return transactionRepository.findAll().stream()
                .filter(transaction -> type == null || transaction.getType() == type)
                .filter(transaction -> status == null || transaction.getStatus() == status)
                .filter(transaction -> !transaction.getCreatedAt().isBefore(fromDateTime))
                .filter(transaction -> !transaction.getCreatedAt().isAfter(toDateTime))
                .sorted((first, second) -> second.getCreatedAt().compareTo(first.getCreatedAt()))
                .map(this::mapTransaction)
                .toList();
    }

    public List<WalletResponseDTO> filterWalletsByStatus(WalletStatus status) {
        List<Wallet> wallets = status == null ? walletRepository.findAll() : walletRepository.findByStatus(status);
        return wallets.stream().map(this::mapWallet).toList();
    }

    public TransactionSummaryResponseDTO getTransactionSummary(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from == null ? LocalDate.MIN.atStartOfDay() : from.atStartOfDay();
        LocalDateTime toDateTime = to == null ? LocalDate.MAX.atTime(LocalTime.MAX) : to.atTime(LocalTime.MAX);

        List<Transaction> filteredTransactions = transactionRepository.findAll().stream()
                .filter(transaction -> !transaction.getCreatedAt().isBefore(fromDateTime))
                .filter(transaction -> !transaction.getCreatedAt().isAfter(toDateTime))
                .toList();

        BigDecimal totalDepositAmount = sumAmountByType(filteredTransactions, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawalAmount = sumAmountByType(filteredTransactions, TransactionType.WITHDRAWAL);
        BigDecimal totalTransferAmount = sumAmountByType(filteredTransactions, TransactionType.TRANSFER);

        return TransactionSummaryResponseDTO.builder()
                .totalTransactions(filteredTransactions.size())
                .successfulTransactions(filteredTransactions.stream().filter(txn -> txn.getStatus() == TransactionStatus.SUCCESS).count())
                .failedTransactions(filteredTransactions.stream().filter(txn -> txn.getStatus() == TransactionStatus.FAILED).count())
                .depositCount(filteredTransactions.stream().filter(txn -> txn.getType() == TransactionType.DEPOSIT).count())
                .withdrawalCount(filteredTransactions.stream().filter(txn -> txn.getType() == TransactionType.WITHDRAWAL).count())
                .transferCount(filteredTransactions.stream().filter(txn -> txn.getType() == TransactionType.TRANSFER).count())
                .totalDepositAmount(totalDepositAmount)
                .totalWithdrawalAmount(totalWithdrawalAmount)
                .totalTransferAmount(totalTransferAmount)
                .build();
    }

    public String exportTransactionsCsv(LocalDate from, LocalDate to) {
        List<TransactionResponseDTO> filtered = filterTransactions(null, null, from, to);
        StringBuilder csv = new StringBuilder();
        csv.append("id,referenceNumber,type,status,amount,senderWalletId,receiverWalletId,description,createdAt\n");

        for (TransactionResponseDTO transaction : filtered) {
            csv.append(transaction.getId()).append(",")
                    .append(transaction.getReferenceNumber()).append(",")
                    .append(transaction.getType()).append(",")
                    .append(transaction.getStatus()).append(",")
                    .append(transaction.getAmount()).append(",")
                    .append(transaction.getSenderWalletId() == null ? "" : transaction.getSenderWalletId()).append(",")
                    .append(transaction.getReceiverWalletId() == null ? "" : transaction.getReceiverWalletId()).append(",")
                    .append(safeCsv(transaction.getDescription())).append(",")
                    .append(transaction.getCreatedAt()).append("\n");
        }

        return csv.toString();
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

    private UserResponseDTO mapUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    private WalletResponseDTO mapWallet(Wallet wallet) {
        return WalletResponseDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    private BigDecimal sumAmountByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private AuditLogResponseDTO mapAudit(AuditLog auditLog) {
        return AuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .performedBy(auditLog.getPerformedBy())
                .entityName(auditLog.getEntityName())
                .description(auditLog.getDescription())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
