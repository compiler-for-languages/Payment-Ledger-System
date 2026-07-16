package com.infotact.project1.config;

import com.infotact.project1.enums.RoleType;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.model.Role;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.RoleRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ADMIN).build()));

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.USER).build()));

        seedUserWithWallet("admin", "admin@infotact.com", "Admin User", adminRole, new BigDecimal("50000.00"));
        seedUserWithWallet("user", "user@infotact.com", "Standard User", userRole, new BigDecimal("10000.00"));
    }

    private void seedUserWithWallet(String username,
                                    String email,
                                    String fullName,
                                    Role role,
                                    BigDecimal openingBalance) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = userRepository.save(User.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .password(passwordEncoder.encode("password"))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build());

        walletRepository.save(Wallet.builder()
                .user(user)
                .balance(openingBalance)
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }
}
