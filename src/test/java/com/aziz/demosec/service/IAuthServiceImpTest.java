package com.aziz.demosec.service;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.Patient;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private LaboratoryRepository laboratoryRepository;
    @Mock
    private LaboratoryStaffRepository laboratoryStaffRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ClinicRepository clinicRepository;
    @Mock
    private PharmacistRepository pharmacistRepository;
    @Mock
    private NutritionistRepository nutritionistRepository;
    @Mock
    private PharmacyRepository pharmacyRepository;
    @Mock
    private HomeCareServiceRepository homeCareServiceRepository;
    @Mock
    private ServiceProviderRepository serviceProviderRepository;
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
        registerRequest = RegisterRequest.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("Password123")
                .role(Role.VISITOR)
                .phone("12345678")
                .build();

        loginRequest = new LoginRequest("john@example.com", "Password123");
    }

    @Test
    void register_ShouldRegisterNewUser_WhenRoleIsUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = authService.register(registerRequest);

        assertNotNull(registeredUser);
        assertEquals("john@example.com", registeredUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldRegisterPatient_WhenRoleIsPatient() {
        RegisterRequest patientRequest = RegisterRequest.builder()
                .fullName("Patient Patient")
                .email("patient@example.com")
                .password("Password123")
                .role(Role.PATIENT)
                .phone("87654321")
                .gender(Gender.MALE)
                .bloodType(BloodType.B_POS)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = authService.register(patientRequest);

        assertTrue(registeredUser instanceof Patient);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userDetails.getUsername()).thenReturn("john@example.com");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_VISITOR"))).when(userDetails).getAuthorities();
        
        // Match the 5-arg signature: UserDetails, String, Long, String, Long
        when(jwtService.generateToken(any(), any(), any(), any(), any())).thenReturn("mocked-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked-token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
