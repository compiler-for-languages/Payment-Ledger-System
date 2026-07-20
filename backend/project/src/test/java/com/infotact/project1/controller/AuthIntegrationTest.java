package com.infotact.project1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.project1.config.TestRedisBeansConfig;
import com.infotact.project1.dto.request.LoginRequestDTO;
import com.infotact.project1.dto.request.RegisterRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisBeansConfig.class)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterThenLoginSuccessfully() throws Exception {
        String unique = String.valueOf(System.currentTimeMillis());

        RegisterRequestDTO registerRequestDTO = RegisterRequestDTO.builder()
                .fullName("Integration User")
                .username("integration" + unique)
                .email("integration" + unique + "@infotact.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(registerRequestDTO.getEmail()));

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(registerRequestDTO.getEmail())
                .password(registerRequestDTO.getPassword())
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value(registerRequestDTO.getUsername()));
    }
}
