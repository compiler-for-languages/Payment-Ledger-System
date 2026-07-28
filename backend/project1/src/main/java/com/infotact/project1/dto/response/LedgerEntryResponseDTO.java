package com.infotact.project1.dto.response;

import com.infotact.project1.enums.LedgerEntryType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryResponseDTO {
    private Long id;
    private Long transactionId;
    private Long walletId;
    private LedgerEntryType entryType;
    private BigDecimal amount;
    private String narration;
    private LocalDateTime createdAt;
}
