package com.infotact.project1.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDTO {
    private long totalUsers;
    private long totalWallets;
    private BigDecimal totalBalance;
    private long todayDeposits;
    private long todayWithdrawals;
    private long todayTransfers;
    private long failedTransactions;
    private long successfulTransactions;
    private long frozenWallets;
    private long blockedWallets;
    private long activeWallets;
}
