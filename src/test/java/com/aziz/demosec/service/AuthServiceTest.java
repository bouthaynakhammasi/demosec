package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.Pharmacist;
import com.aziz.demosec.Entities.Pharmacy;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.*;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PharmacistRepository pharmacistRepository;
    @Mock
    private PharmacyRepository pharmacyRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ServiceProviderRepository serviceProviderRepository;
    @Mock
    private HomeCareServiceRepository homeCareServiceRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest patientRegisterRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        patientRegisterRequest = new RegisterRequest(
                "Aziz Test",
                "aziz@test.com",
                "password123",
                Role.PATIENT,
                "12345678",
                LocalDate.of(1990, 1, 1),
                null, null, null, null, null, null, null,
                null, null, null,
                null
        );

        loginRequest = new LoginRequest("aziz@test.com", "password123");
    }

    @Test
    void register_ShouldSavePatient_WhenRoleIsPatient() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authService.register(patientRegisterRequest, null);

        // Assert
        assertNotNull(result);
        assertEquals(Role.PATIENT, result.getRole());
        assertEquals("aziz@test.com", result.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("aziz@test.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(patientRegisterRequest, null));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setId(1L);
        user.setEmail("aziz@test.com");

        when(userDetailsService.loadUserByUsername("aziz@test.com")).thenReturn(userDetails);
        when(userRepository.findByEmail("aziz@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class), anyLong())).thenReturn("mockToken");
        when(userDetails.getUsername()).thenReturn("aziz@test.com");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptySet());

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mockToken", response.token());
        assertEquals("aziz@test.com", response.email());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
