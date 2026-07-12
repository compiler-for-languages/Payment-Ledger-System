package com.infotact.project1.repository;

import com.infotact.project1.model.LedgerEntry;
import com.infotact.project1.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
