package com.infotact.project1.controller;

import com.infotact.project1.dto.response.WalletResponseDTO;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.User;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @Operation(summary = "Get wallet for authenticated user")
    @GetMapping("/me")
    public ResponseEntity<WalletResponseDTO> getMyWallet(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));
        return ResponseEntity.ok(walletService.getWalletByUserId(user.getId()));
    }

    @Operation(summary = "Freeze wallet by id")
    @PatchMapping("/{walletId}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> freezeWallet(@PathVariable Long walletId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.freezeWallet(walletId, userDetails.getUsername()));
    }

    @Operation(summary = "Activate wallet by id")
    @PatchMapping("/{walletId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> activateWallet(@PathVariable Long walletId,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.activateWallet(walletId, userDetails.getUsername()));
    }

    @Operation(summary = "Lock wallet by id")
    @PatchMapping("/{walletId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> lockWallet(@PathVariable Long walletId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.lockWallet(walletId, userDetails.getUsername()));
    }

    @Operation(summary = "Unlock wallet by id")
    @PatchMapping("/{walletId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> unlockWallet(@PathVariable Long walletId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.unlockWallet(walletId, userDetails.getUsername()));
    }
}
