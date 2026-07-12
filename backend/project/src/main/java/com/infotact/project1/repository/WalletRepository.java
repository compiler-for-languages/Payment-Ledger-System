package com.infotact.project1.repository;

import com.infotact.project1.enums.WalletStatus;
import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);

    boolean existsByUser(User user);

    long countByStatus(WalletStatus status);

    List<Wallet> findByStatus(WalletStatus status);
}
