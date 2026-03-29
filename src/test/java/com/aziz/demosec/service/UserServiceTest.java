package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.repository.PharmacistRepository;
import com.aziz.demosec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PharmacistRepository pharmacistRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO userRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userRequestDTO = UserRequestDTO.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("password123")
                .role(Role.PATIENT)
                .phone("12345678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .role(Role.PATIENT)
                .enabled(true)
                .build();
    }

    @Test
    void create_ShouldReturnUserResponse_WhenEmailIsNew() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO response = userService.create(userRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.create(userRequestDTO));
    }

    @Test
    void getById_ShouldReturnUser_WhenExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO response = userService.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void toggleEnabled_ShouldFlipStatus() {
        // Arrange
        boolean initialStatus = user.isEnabled();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.toggleEnabled(1L);

        // Assert
        assertEquals(!initialStatus, user.isEnabled());
        verify(userRepository, times(1)).save(user);
    }
}
