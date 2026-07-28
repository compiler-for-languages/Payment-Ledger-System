package com.infotact.project1.repository;

import com.infotact.project1.model.User;
import com.infotact.project1.model.Wallet;
import com.infotact.project1.enums.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);

    boolean existsByUser(User user);

    long countByStatus(WalletStatus status);

    List<Wallet> findByStatus(WalletStatus status);
}
