package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .password("encoded_password")
                .role(Role.VISITOR)
                .phone("123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .enabled(true)
                .build();

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFullName("John Doe");
        userRequestDTO.setEmail("john@example.com");
        userRequestDTO.setPassword("password");
        userRequestDTO.setRole(Role.VISITOR);
        userRequestDTO.setPhone("123456789");
        userRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    void create_ShouldReturnUserResponseDTO_WhenEmailIsUnique() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.create(userRequestDTO);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.create(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_ShouldReturnUserResponseDTO_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getById(1L);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getAll_ShouldReturnListOfUserResponseDTO() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserResponseDTO> responseList = userService.getAll();

        assertFalse(responseList.isEmpty());
        assertEquals(1, responseList.size());
    }

    @Test
    void update_ShouldReturnUserResponseDTO_WhenUserExistsAndEmailIsUnique() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userRequestDTO.setEmail("new_john@example.com");

        UserResponseDTO response = userService.update(1L, userRequestDTO);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenEmailIsInUse() {
        user.setEmail("old@example.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.update(1L, userRequestDTO));
    }

    @Test
    void delete_ShouldCallRepositoryDelete_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void toggleEnabled_ShouldToggleStatusAndSave() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        
        boolean initialStatus = user.isEnabled();
        // Since toggle modifies the original object we check its status before to mimic behaviour.
        userService.toggleEnabled(1L);

        verify(userRepository).save(argThat(savedUser -> savedUser.isEnabled() != initialStatus));
    }

    @Test
    void getByEmail_ShouldReturnUserResponseDTO_WhenUserExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getByEmail("john@example.com");

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void updateByEmail_ShouldReturnUserResponseDTO_WhenUserExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.updateByEmail("john@example.com", userRequestDTO);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }
}
