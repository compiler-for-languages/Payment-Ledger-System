package com.infotact.project1.service;

import com.infotact.project1.dto.response.WalletResponseDTO;
import com.infotact.project1.enums.AuditActionType;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public WalletResponseDTO getWalletByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessExceptions.userNotFound(userId));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(userId));

        return mapWallet(wallet);
    }

    @Transactional
    public WalletResponseDTO freezeWallet(Long walletId, String performedBy) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(walletId));

        wallet.setStatus(WalletStatus.FROZEN);

        auditLogService.createAuditLog(
                AuditActionType.WALLET_FROZEN,
                performedBy,
                "Wallet",
                "Wallet frozen with id: " + walletId
        );

        return mapWallet(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponseDTO activateWallet(Long walletId, String performedBy) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(walletId));

        wallet.setStatus(WalletStatus.ACTIVE);

        auditLogService.createAuditLog(
                AuditActionType.WALLET_ACTIVATED,
                performedBy,
                "Wallet",
                "Wallet activated with id: " + walletId
        );

        return mapWallet(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponseDTO lockWallet(Long walletId, String performedBy) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(walletId));

        wallet.setStatus(WalletStatus.BLOCKED);

        auditLogService.createAuditLog(
                AuditActionType.WALLET_LOCKED,
                performedBy,
                "Wallet",
                "Wallet locked with id: " + walletId
        );

        return mapWallet(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponseDTO unlockWallet(Long walletId, String performedBy) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> BusinessExceptions.walletNotFound(walletId));

        wallet.setStatus(WalletStatus.ACTIVE);

        auditLogService.createAuditLog(
                AuditActionType.WALLET_UNLOCKED,
                performedBy,
                "Wallet",
                "Wallet unlocked with id: " + walletId
        );

        return mapWallet(walletRepository.save(wallet));
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
}
