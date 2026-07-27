package com.infotact.project1.service;

import com.infotact.project1.dto.response.WalletResponseDTO;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.exception.BusinessException;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldFreezeWalletSuccessfully() {
        User user = User.builder().id(1L).build();
        Wallet wallet = Wallet.builder()
                .id(10L)
                .user(user)
                .balance(BigDecimal.TEN)
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(walletRepository.findById(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponseDTO responseDTO = walletService.freezeWallet(10L, "admin@infotact.com");

        assertEquals(WalletStatus.FROZEN, responseDTO.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        when(walletRepository.findById(404L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> walletService.freezeWallet(404L, "admin@infotact.com"));

        assertEquals(404, exception.getStatus().value());
    }
}
