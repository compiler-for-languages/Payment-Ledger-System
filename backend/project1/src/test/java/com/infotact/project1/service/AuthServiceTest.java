package com.infotact.project1.service;

import com.infotact.project1.dto.request.RegisterRequestDTO;
import com.infotact.project1.dto.response.AuthResponseDTO;
import com.infotact.project1.enums.RoleType;
import com.infotact.project1.exception.BusinessException;
import com.infotact.project1.model.Role;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.RoleRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import com.infotact.project1.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequestDTO;

    @BeforeEach
    void setUp() {
        registerRequestDTO = RegisterRequestDTO.builder()
                .fullName("Test User")
                .username("testuser")
                .email("test@infotact.com")
                .password("password")
                .build();
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequestDTO.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(registerRequestDTO));

        assertEquals(409, exception.getStatus().value());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Role role = Role.builder().id(1L).name(RoleType.USER).build();
        User savedUser = User.builder()
                .id(10L)
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .fullName(registerRequestDTO.getFullName())
                .role(role)
                .build();

        when(userRepository.existsByEmail(registerRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequestDTO.getUsername())).thenReturn(false);
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(walletRepository.save(any(Wallet.class))).thenReturn(Wallet.builder().id(101L).build());
        when(jwtService.generateToken(savedUser.getEmail())).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register(registerRequestDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(savedUser.getEmail(), response.getUser().getEmail());
    }
}
