package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.LabRequestMapper;
import com.aziz.demosec.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabRequestServiceImplTest {

    @Mock
    private LabRequestRepository labRequestRepository;
    @Mock
    private LaboratoryRepository laboratoryRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private LabRequestMapper labRequestMapper;

    @InjectMocks
    private LabRequestServiceImpl labRequestService;

    private LabRequestRequest request;
    private Patient patient;
    private Laboratory laboratory;
    private LabRequest labRequest;

    @BeforeEach
    void setUp() {
        request = LabRequestRequest.builder()
                .patientId(1L)
                .laboratoryId(1L)
                .testType("Blood Test")
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .build();

        patient = new Patient();
        patient.setId(1L);
        patient.setFullName("John Doe");

        laboratory = new Laboratory();
        laboratory.setId(1L);
        laboratory.setName("Central Lab");
        laboratory.setActive(true);

        labRequest = new LabRequest();
        labRequest.setId(1L);
        labRequest.setPatient(patient);
        labRequest.setLaboratory(laboratory);
        labRequest.setTestType("Blood Test");
        labRequest.setStatus(LabRequestStatus.PENDING);
    }

    @Test
    void create_ShouldCreateLabRequest_WhenValidRequest() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(labRequestMapper.toEntity(any(LabRequestRequest.class))).thenReturn(labRequest);
        when(labRequestRepository.save(any(LabRequest.class))).thenReturn(labRequest);
        when(labRequestMapper.toDto(any(LabRequest.class))).thenReturn(new LabRequestResponse());

        // Act
        LabRequestResponse response = labRequestService.create(request);

        // Assert
        assertNotNull(response);
        verify(labRequestRepository).save(any(LabRequest.class));
    }

    @Test
    void create_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> labRequestService.create(request));
    }

    @Test
    void create_ShouldThrowException_WhenLaboratoryInactive() {
        // Arrange
        laboratory.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> labRequestService.create(request));
        assertTrue(exception.getMessage().contains("is currently inactive"));
    }

    @Test
    void getById_ShouldReturnResponse_WhenFound() {
        // Arrange
        when(labRequestRepository.findById(1L)).thenReturn(Optional.of(labRequest));
        when(labRequestMapper.toDto(labRequest)).thenReturn(new LabRequestResponse());

        // Act
        LabRequestResponse response = labRequestService.getById(1L);

        // Assert
        assertNotNull(response);
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenTransitionIsValid() {
        // Arrange
        when(labRequestRepository.findById(1L)).thenReturn(Optional.of(labRequest));
        when(labRequestRepository.save(any(LabRequest.class))).thenReturn(labRequest);
        when(labRequestMapper.toDto(any(LabRequest.class))).thenReturn(new LabRequestResponse());

        // Act
        LabRequestResponse response = labRequestService.updateStatus(1L, LabRequestStatus.IN_PROGRESS);

        // Assert
        assertNotNull(response);
        assertEquals(LabRequestStatus.IN_PROGRESS, labRequest.getStatus());
    }

    @Test
    void updateStatus_ShouldThrowException_WhenCurrentStatusIsCompleted() {
        // Arrange
        labRequest.setStatus(LabRequestStatus.COMPLETED);
        when(labRequestRepository.findById(1L)).thenReturn(Optional.of(labRequest));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> labRequestService.updateStatus(1L, LabRequestStatus.PENDING));
    }
}
