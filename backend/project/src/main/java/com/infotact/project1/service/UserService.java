package com.infotact.project1.service;

import com.infotact.project1.dto.request.ChangePasswordRequestDTO;
import com.infotact.project1.dto.request.UpdateProfileRequestDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.enums.AuditActionType;
import com.infotact.project1.exception.BusinessExceptions;
import com.infotact.project1.model.User;
import com.infotact.project1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserResponseDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));
        return mapUser(user);
    }

    @Transactional
    public UserResponseDTO updateProfile(String email, UpdateProfileRequestDTO requestDTO) {
        // Profile updates validate username uniqueness before updating mutable user identity fields.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));

        userRepository.findByUsername(requestDTO.getUsername())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw BusinessExceptions.duplicateUsername(requestDTO.getUsername());
                });

        user.setFullName(requestDTO.getFullName());
        user.setUsername(requestDTO.getUsername());

        User savedUser = userRepository.save(user);
        return mapUser(savedUser);
    }

    @Transactional
    public String changePassword(String email, ChangePasswordRequestDTO requestDTO) {
        // Password change validates old password first and emits audit log on success.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessExceptions.userNotFound(-1L));

        if (!passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())) {
            throw BusinessExceptions.invalidCredentials();
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        auditLogService.createAuditLog(
                AuditActionType.PASSWORD_CHANGED,
                email,
                "User",
                "User changed account password"
        );

        return "Password changed successfully";
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
