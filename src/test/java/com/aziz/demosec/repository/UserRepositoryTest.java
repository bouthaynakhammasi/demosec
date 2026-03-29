package com.aziz.demosec.repository;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = User.builder()
                .fullName("John Doe")
                .email("test@example.com")
                .password("password")
                .role(Role.VISITOR)
                .enabled(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getFullName());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        // Arrange
        User user = User.builder()
                .fullName("John Doe")
                .email("exists@example.com")
                .password("password")
                .role(Role.VISITOR)
                .enabled(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // Assert
        assertTrue(exists);
    }
}
