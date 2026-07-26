package com.infotact.project1.service;

import com.infotact.project1.dto.request.AmountRequestDTO;
import com.infotact.project1.dto.request.TransferRequestDTO;
import com.infotact.project1.dto.response.TransactionResponseDTO;
import com.infotact.project1.enums.RoleType;
import com.infotact.project1.enums.TransactionStatus;
import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.exception.BusinessException;
import com.infotact.project1.model.Role;
import com.infotact.project1.model.Transaction;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.repository.LedgerEntryRepository;
import com.infotact.project1.repository.TransactionRepository;
import com.infotact.project1.repository.UserRepository;
import com.infotact.project1.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LedgerEntryRepository ledgerEntryRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RLock lock;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).name(RoleType.USER).build();

        sender = User.builder().id(1L).email("sender@infotact.com").role(role).build();
        receiver = User.builder().id(2L).email("receiver@infotact.com").role(role).build();

        senderWallet = Wallet.builder()
                .id(11L)
                .user(sender)
                .balance(new BigDecimal("1000.00"))
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        receiverWallet = Wallet.builder()
                .id(12L)
                .user(receiver)
                .balance(new BigDecimal("100.00"))
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(redissonClient.getLock(any(String.class))).thenReturn(lock);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldThrowExceptionForInvalidDepositAmount() {
        AmountRequestDTO requestDTO = AmountRequestDTO.builder().amount(BigDecimal.ZERO).build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.deposit(1L, requestDTO));

        assertEquals(400, exception.getStatus().value());
    }

    @Test
    void shouldReturnFailedWithdrawalTransactionWhenInsufficientBalance() {
        AmountRequestDTO requestDTO = AmountRequestDTO.builder().amount(new BigDecimal("5000.00")).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDTO response = transactionService.withdraw(1L, requestDTO);

        assertNotNull(response);
        assertEquals(TransactionStatus.FAILED, response.getStatus());
    }

    @Test
    void shouldRejectDuplicateTransferByIdempotencyKey() {
        TransferRequestDTO requestDTO = TransferRequestDTO.builder()
                .receiverUserId(2L)
                .amount(new BigDecimal("10.00"))
                .description("test")
                .build();

        Transaction existingTransaction = Transaction.builder()
                .id(99L)
                .referenceNumber("TXN-existing")
                .status(TransactionStatus.SUCCESS)
                .amount(new BigDecimal("10.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));
        when(valueOperations.get("idempotency:dup-key")).thenReturn("TXN-existing");
        when(transactionRepository.findByReferenceNumber("TXN-existing")).thenReturn(Optional.of(existingTransaction));

        TransactionResponseDTO response = transactionService.transfer(1L, requestDTO, "dup-key");

        assertEquals("TXN-existing", response.getReferenceNumber());
    }

    @Test
    void shouldThrowExceptionWhenTransferLockNotAcquired() throws Exception {
        TransferRequestDTO requestDTO = TransferRequestDTO.builder()
                .receiverUserId(2L)
                .amount(new BigDecimal("10.00"))
                .description("lock test")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));
        when(valueOperations.get("idempotency:new-key")).thenReturn(null);
        when(redissonClient.getLock(eq("wallet:11"))).thenReturn(lock);
        when(redissonClient.getLock(eq("wallet:12"))).thenReturn(lock);
        when(lock.tryLock(3, 10, java.util.concurrent.TimeUnit.SECONDS)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.transfer(1L, requestDTO, "new-key"));

        assertEquals(400, exception.getStatus().value());
    }
}
