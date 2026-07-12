package com.infotact.project1.repository;

import com.infotact.project1.enums.RoleType;
import com.infotact.project1.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
