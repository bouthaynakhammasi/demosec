package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LaboratoryStaffRepository;
import com.aziz.demosec.repository.PasswordResetTokenRepository;
import com.aziz.demosec.repository.PatientRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IAuthServiceImpTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private LaboratoryRepository laboratoryRepository;
    @Mock
    private LaboratoryStaffRepository laboratoryStaffRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private IAuthServiceImp authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "Password123",
                Role.VISITOR,
                "12345678",
                LocalDate.of(1990, 1, 1),
                null, null, null, null, null, null, null, null, null, null
        );

        loginRequest = new LoginRequest("john@example.com", "Password123");
    }

    @Test
    void register_ShouldRegisterNewUser_WhenRoleIsUser() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User registeredUser = authService.register(registerRequest);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("john@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(Role.VISITOR, registeredUser.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldRegisterPatient_WhenRoleIsPatient() {
        // Arrange
        RegisterRequest patientRequest = new RegisterRequest(
                "Patient Patient",
                "patient@example.com",
                "Password123",
                Role.PATIENT,
                "87654321",
                LocalDate.of(1995, 5, 5),
                com.aziz.demosec.Entities.Gender.MALE, com.aziz.demosec.Entities.BloodType.B_POS, "Emergency contact", "111222333", null, null, null, null, null, null
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User registeredUser = authService.register(patientRequest);

        // Assert
        assertTrue(registeredUser instanceof Patient);
        assertEquals("patient@example.com", registeredUser.getEmail());
        assertEquals(Role.PATIENT, registeredUser.getRole());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });
        assertEquals("Email already used", exception.getMessage());
    }

    @Test
    void register_ShouldThrowException_WhenPasswordTooShort() {
        // Arrange
        RegisterRequest shortPasswordRequest = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "123",
                Role.VISITOR,
                "12345678",
                LocalDate.of(1990, 1, 1),
                null, null, null, null, null, null, null, null, null, null
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(shortPasswordRequest);
        });
        assertEquals("Password must contain at least 8 characters", exception.getMessage());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userDetails.getUsername()).thenReturn("john@example.com");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_VISITOR"))).when(userDetails).getAuthorities();
        when(jwtService.generateToken(any(org.springframework.security.core.userdetails.UserDetails.class), any(String.class), any(Long.class), any(), any())).thenReturn("mocked-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-token", response.token());
        assertEquals("john@example.com", response.email());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
