package com.infotact.project1.service;

import com.infotact.project1.dto.request.LoginRequestDTO;
import com.infotact.project1.dto.request.RegisterRequestDTO;
import com.infotact.project1.dto.response.AuthResponseDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.enums.AuditActionType;
import com.infotact.project1.enums.RoleType;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.Role;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.RoleRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import com.infotact.project1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        // Registration flow validates duplicate identity, creates user+wallet, and returns JWT.
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw BusinessExceptions.duplicateEmail(requestDTO.getEmail());
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw BusinessExceptions.duplicateUsername(requestDTO.getUsername());
        }

        Role role = roleRepository.findByName(RoleType.USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.USER).build()));

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        String token = jwtService.generateToken(savedUser.getEmail());

        auditLogService.createAuditLog(
                AuditActionType.USER_REGISTERED,
                savedUser.getEmail(),
                "User",
                "User registered with wallet id pending assignment"
        );

        return AuthResponseDTO.builder()
                .token(token)
                .user(mapUser(savedUser))
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        // Login flow delegates authentication manager and issues JWT only on successful credentials.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
        );

        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(BusinessExceptions::invalidCredentials);

        String token = jwtService.generateToken(user.getEmail());

        auditLogService.createAuditLog(
                AuditActionType.USER_LOGGED_IN,
                user.getEmail(),
                "User",
                "User logged in successfully"
        );

        return AuthResponseDTO.builder()
                .token(token)
                .user(mapUser(user))
                .build();
    }

    private UserResponseDTO mapUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }
}
