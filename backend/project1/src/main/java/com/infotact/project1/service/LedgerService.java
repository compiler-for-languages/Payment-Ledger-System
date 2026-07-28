package com.infotact.project1.service;

import com.infotact.project1.dto.response.LedgerEntryResponseDTO;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.LedgerEntry;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.LedgerEntryRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public List<LedgerEntryResponseDTO> getLedgerEntriesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessExceptions.userNotFound(userId));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(userId));

        return ledgerEntryRepository.findByWalletOrderByCreatedAtDesc(wallet)
                .stream()
                .map(this::mapEntry)
                .toList();
    }

    private LedgerEntryResponseDTO mapEntry(LedgerEntry entry) {
        return LedgerEntryResponseDTO.builder()
                .id(entry.getId())
                .transactionId(entry.getTransaction().getId())
                .walletId(entry.getWallet().getId())
                .entryType(entry.getEntryType())
                .amount(entry.getAmount())
                .narration(entry.getNarration())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
