package com.infotact.project1.controller;

import com.infotact.project1.dto.response.LedgerEntryResponseDTO;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.User;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final UserRepository userRepository;

    @Operation(summary = "Get ledger entries for authenticated user")
    @GetMapping("/me")
    public ResponseEntity<List<LedgerEntryResponseDTO>> getMyLedgerEntries(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));
        return ResponseEntity.ok(ledgerService.getLedgerEntriesForUser(user.getId()));
    }
}
