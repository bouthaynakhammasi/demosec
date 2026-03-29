package com.aziz.demosec.service;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.dto.patient.PatientRequestDTO;
import com.aziz.demosec.dto.patient.PatientResponseDTO;
import com.aziz.demosec.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientRequestDTO requestDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        requestDTO = PatientRequestDTO.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("password123")
                .phone("12345678")
                .gender(Gender.FEMALE)
                .bloodType(BloodType.A_POS)
                .build();

        patient = new Patient();
        patient.setId(1L);
        patient.setFullName("Jane Doe");
        patient.setEmail("jane@example.com");
        patient.setEnabled(true);
    }

    @Test
    void create_ShouldCreatePatient_WhenEmailIsUnique() {
        // Arrange
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        PatientResponseDTO response = patientService.create(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("jane@example.com", response.getEmail());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientService.create(requestDTO));
    }

    @Test
    void getById_ShouldReturnPatient_WhenExists() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientResponseDTO response = patientService.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.getById(1L));
    }

    @Test
    void toggleEnabled_ShouldFlipEnabledStatus() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        patientService.toggleEnabled(1L);

        // Assert
        assertFalse(patient.isEnabled());
        verify(patientRepository).save(patient);
    }
}
