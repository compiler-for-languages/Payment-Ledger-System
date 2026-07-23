package com.infotact.project1.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryResponseDTO {
    private long totalTransactions;
    private long successfulTransactions;
    private long failedTransactions;
    private long depositCount;
    private long withdrawalCount;
    private long transferCount;
    private BigDecimal totalDepositAmount;
    private BigDecimal totalWithdrawalAmount;
    private BigDecimal totalTransferAmount;
}
