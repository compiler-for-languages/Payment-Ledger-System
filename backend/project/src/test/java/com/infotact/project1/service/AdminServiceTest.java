package com.infotact.project1.service;

import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.model.Transaction;
import com.infotact.project1.repository.AuditLogRepository;
import com.infotact.project1.repository.TransactionRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldFilterTransactionsByTypeAndStatusAndDateRange() {
        Transaction inRange = Transaction.builder()
                .id(1L)
                .referenceNumber("TXN-1")
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .amount(new BigDecimal("10.00"))
                .description("in-range")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Transaction outOfRange = Transaction.builder()
                .id(2L)
                .referenceNumber("TXN-2")
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.FAILED)
                .amount(new BigDecimal("20.00"))
                .description("out-range")
                .createdAt(LocalDateTime.now().minusMonths(2))
                .build();

        when(transactionRepository.findAll()).thenReturn(List.of(inRange, outOfRange));

        List<TransactionResponseDTO> result = adminService.filterTransactions(
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS,
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );

        assertEquals(1, result.size());
        assertEquals("TXN-1", result.get(0).getReferenceNumber());
    }

    @Test
    void shouldExportTransactionsCsv() {
        Transaction transaction = Transaction.builder()
                .id(10L)
                .referenceNumber("TXN-CSV")
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .amount(new BigDecimal("50.00"))
                .description("csv export")
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        String csv = adminService.exportTransactionsCsv(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertTrue(csv.contains("TXN-CSV"));
        assertTrue(csv.contains("type,status"));
    }
}
