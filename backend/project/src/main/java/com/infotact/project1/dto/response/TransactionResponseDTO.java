package com.infotact.project1.dto.response;

import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private String referenceNumber;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private Long senderWalletId;
    private Long receiverWalletId;
    private String description;
    private LocalDateTime createdAt;
}
