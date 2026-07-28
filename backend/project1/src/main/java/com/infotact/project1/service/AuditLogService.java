package com.infotact.project1.service;

import com.infotact.project1.enums.AuditActionType;
import com.infotact.project1.model.AuditLog;
import com.infotact.project1.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void createAuditLog(AuditActionType action, String performedBy, String entityName, String description) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .entityName(entityName)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }
}
