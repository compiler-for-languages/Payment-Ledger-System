package com.infotact.project1.dto.response;

import com.infotact.project1.enums.WalletStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDTO {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private WalletStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


