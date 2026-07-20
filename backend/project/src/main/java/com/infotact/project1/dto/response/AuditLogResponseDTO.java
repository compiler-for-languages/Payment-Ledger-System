package com.infotact.project1.dto.response;

import com.infotact.project1.enums.AuditActionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {
    private Long id;
    private AuditActionType action;
    private String performedBy;
    private String entityName;
    private String description;
    private LocalDateTime timestamp;
}
