package com.infotact.project1.controller;

import com.infotact.project1.dto.response.AuditLogResponseDTO;
import com.infotact.project1.dto.response.DashboardStatsResponseDTO;
import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.dto.response.TransactionSummaryResponseDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.dto.response.WalletResponseDTO;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get admin dashboard statistics")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponseDTO> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "Get users with pagination")
    @GetMapping("/users/paged")
    public ResponseEntity<List<UserResponseDTO>> getUsersPaged(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getUsersPaged(page, size));
    }

    @Operation(summary = "Search users by name, username, or email")
    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchUsers(keyword));
    }

    @Operation(summary = "Get all wallets")
    @GetMapping("/wallets")
    public ResponseEntity<List<WalletResponseDTO>> getAllWallets() {
        return ResponseEntity.ok(adminService.getAllWallets());
    }

    @Operation(summary = "Get wallets with pagination")
    @GetMapping("/wallets/paged")
    public ResponseEntity<List<WalletResponseDTO>> getWalletsPaged(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getWalletsPaged(page, size));
    }

    @Operation(summary = "Filter wallets by status")
    @GetMapping("/wallets/filter")
    public ResponseEntity<List<WalletResponseDTO>> filterWallets(@RequestParam(required = false) WalletStatus status) {
        return ResponseEntity.ok(adminService.filterWalletsByStatus(status));
    }

    @Operation(summary = "Get all transactions")
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @Operation(summary = "Get transactions with pagination")
    @GetMapping("/transactions/paged")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsPaged(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getTransactionsPaged(page, size));
    }

    @Operation(summary = "Search transactions by reference or description")
    @GetMapping("/transactions/search")
    public ResponseEntity<List<TransactionResponseDTO>> searchTransactions(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchTransactions(keyword));
    }

    @Operation(summary = "Filter transactions by type, status and date range")
    @GetMapping("/transactions/filter")
    public ResponseEntity<List<TransactionResponseDTO>> filterTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(adminService.filterTransactions(type, status, from, to));
    }

    @Operation(summary = "Get failed transactions")
    @GetMapping("/transactions/failed")
    public ResponseEntity<List<TransactionResponseDTO>> getFailedTransactions() {
        return ResponseEntity.ok(adminService.getFailedTransactions());
    }

    @Operation(summary = "Get audit logs")
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }

    @Operation(summary = "Get transaction summary report")
    @GetMapping("/reports/summary")
    public ResponseEntity<TransactionSummaryResponseDTO> getSummaryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(adminService.getTransactionSummary(from, to));
    }

    @Operation(summary = "Export transaction report as CSV")
    @GetMapping("/reports/transactions.csv")
    public ResponseEntity<String> exportTransactionsCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String csvBody = adminService.exportTransactionsCsv(from, to);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.csv")
                .body(csvBody);
    }
}
