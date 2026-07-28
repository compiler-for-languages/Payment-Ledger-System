package com.infotact.project1.controller;

import com.infotact.project1.dto.response.DashboardStatsResponseDTO;
import com.infotact.project1.dto.response.TransactionSummaryResponseDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.enums.RoleType;
import com.infotact.project1.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void shouldReturnDashboardStatsForAdmin() throws Exception {
        DashboardStatsResponseDTO responseDTO = DashboardStatsResponseDTO.builder()
                .totalUsers(100)
                .totalWallets(100)
                .totalBalance(new BigDecimal("10000.00"))
                .build();

        when(adminService.getDashboardStats()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100));
    }

    @Test
    void shouldSearchUsersForAdmin() throws Exception {
        when(adminService.searchUsers("john")).thenReturn(List.of(
                UserResponseDTO.builder()
                        .id(1L)
                        .fullName("John Doe")
                        .username("john")
                        .email("john@infotact.com")
                        .role(RoleType.USER)
                        .build()
        ));

        mockMvc.perform(get("/api/admin/users/search").param("keyword", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void shouldReturnSummaryReport() throws Exception {
        when(adminService.getTransactionSummary(null, null)).thenReturn(
                TransactionSummaryResponseDTO.builder()
                        .totalTransactions(5)
                        .successfulTransactions(4)
                        .failedTransactions(1)
                        .build()
        );

        mockMvc.perform(get("/api/admin/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactions").value(5))
                .andExpect(jsonPath("$.failedTransactions").value(1));
    }

    @Test
    void shouldExportTransactionCsv() throws Exception {
        when(adminService.exportTransactionsCsv(null, null)).thenReturn("id,referenceNumber\n1,TXN-1\n");

        mockMvc.perform(get("/api/admin/reports/transactions.csv"))
                .andExpect(status().isOk())
                .andExpect(content().string("id,referenceNumber\n1,TXN-1\n"));
    }
}
