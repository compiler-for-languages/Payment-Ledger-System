package com.infotact.project1.repository;

import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.model.Transaction;
import com.infotact.project1.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    List<Transaction> findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(Wallet senderWallet, Wallet receiverWallet);

    long countByStatusAndCreatedAtBetween(TransactionStatus status, LocalDateTime start, LocalDateTime end);

    long countByTypeAndCreatedAtBetween(TransactionType type, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);

    List<Transaction> findByReferenceNumberContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
            String reference,
            String description
    );
}
