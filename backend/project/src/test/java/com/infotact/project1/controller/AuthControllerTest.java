package com.infotact.project1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotact.project1.dto.request.RegisterRequestDTO;
import com.infotact.project1.dto.response.AuthResponseDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.enums.RoleType;
import com.infotact.project1.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequestDTO requestDTO = RegisterRequestDTO.builder()
                .fullName("Test User")
                .username("testuser")
                .email("test@infotact.com")
                .password("password")
                .build();

        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .token("jwt-token")
                .user(UserResponseDTO.builder()
                        .id(1L)
                        .fullName("Test User")
                        .username("testuser")
                        .email("test@infotact.com")
                        .role(RoleType.USER)
                        .build())
                .build();

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@infotact.com"));
    }
}
