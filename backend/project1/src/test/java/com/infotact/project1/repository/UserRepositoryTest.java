package com.infotact.project1.repository;

import com.infotact.project1.enums.RoleType;
import com.infotact.project1.model.Role;
import com.infotact.project1.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindUserByEmail() {
        Role role = roleRepository.save(Role.builder().name(RoleType.USER).build());

        userRepository.save(User.builder()
                .fullName("Repo User")
                .username("repouser")
                .email("repo@infotact.com")
                .password("encoded")
                .role(role)
                .createdAt(LocalDateTime.now())
                .build());

        Optional<User> found = userRepository.findByEmail("repo@infotact.com");

        assertTrue(found.isPresent());
    }
}
