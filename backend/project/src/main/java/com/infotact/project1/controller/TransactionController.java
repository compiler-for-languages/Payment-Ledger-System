package com.infotact.project1.controller;

import com.infotact.project1.dto.request.AmountRequestDTO;
import com.infotact.project1.dto.request.TransferRequestDTO;
import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.User;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @Operation(summary = "Deposit into authenticated user wallet")
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@AuthenticationPrincipal UserDetails userDetails,
                                                          @Valid @RequestBody AmountRequestDTO requestDTO) {
        User user = fetchUser(userDetails);
        return ResponseEntity.ok(transactionService.deposit(user.getId(), requestDTO));
    }

    @Operation(summary = "Withdraw from authenticated user wallet")
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody AmountRequestDTO requestDTO) {
        User user = fetchUser(userDetails);
        return ResponseEntity.ok(transactionService.withdraw(user.getId(), requestDTO));
    }

    @Operation(summary = "Transfer amount to another user wallet")
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody TransferRequestDTO requestDTO,
                                                           @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        User user = fetchUser(userDetails);
        return ResponseEntity.ok(transactionService.transfer(user.getId(), requestDTO, idempotencyKey));
    }

    @Operation(summary = "Get transaction history for authenticated user")
    @GetMapping("/me")
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = fetchUser(userDetails);
        return ResponseEntity.ok(transactionService.getTransactions(user.getId()));
    }

    @Operation(summary = "Filter authenticated user transactions")
    @GetMapping("/me/filter")
    public ResponseEntity<List<TransactionResponseDTO>> filterMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        User user = fetchUser(userDetails);
        return ResponseEntity.ok(transactionService.filterMyTransactions(user.getId(), type, status, from, to));
    }

    @Operation(summary = "Get transaction by reference number")
    @GetMapping("/{referenceNumber}")
    public ResponseEntity<TransactionResponseDTO> getByReference(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(transactionService.getTransactionByReference(referenceNumber));
    }

    private User fetchUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));
    }
}
