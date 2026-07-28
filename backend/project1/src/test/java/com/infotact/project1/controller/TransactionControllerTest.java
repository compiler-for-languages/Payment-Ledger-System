package com.infotact.project1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.project1.dto.request.AmountRequestDTO;
import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.TransactionType;
import com.infotact.project1.model.User;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldDepositForAuthenticatedUser() throws Exception {
        AmountRequestDTO requestDTO = AmountRequestDTO.builder()
                .amount(new BigDecimal("100.00"))
                .description("Deposit")
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id(1L)
                .referenceNumber("TXN-1")
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("user@infotact.com")).thenReturn(Optional.of(User.builder().id(1L).email("user@infotact.com").build()));
        when(transactionService.deposit(eq(1L), any(AmountRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transactions/deposit")
                        .with(user("user@infotact.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").value("TXN-1"));
    }
}
