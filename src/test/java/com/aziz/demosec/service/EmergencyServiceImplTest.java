package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.emergency.*;
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
public class EmergencyServiceImplTest {

    @Mock
    private SmartDeviceRepository smartDeviceRepository;
    @Mock
    private EmergencyAlertRepository alertRepository;
    @Mock
    private AmbulanceRepository ambulanceRepository;
    @Mock
    private EmergencyInterventionRepository interventionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private EmergencyServiceImpl emergencyService;

    private User samplePatient;
    private SmartDevice sampleDevice;
    private EmergencyAlert sampleAlert;
    private Clinic sampleClinic;
    private Ambulance sampleAmbulance;

    @BeforeEach
    public void setup() {
        samplePatient = new User();
        samplePatient.setId(10L);
        samplePatient.setFullName("John Doe");

        sampleDevice = SmartDevice.builder()
                .id(100L)
                .patient(samplePatient)
                .build();

        sampleAlert = EmergencyAlert.builder()
                .id(50L)
                .device(sampleDevice)
                .severity(EmergencySeverity.CRITICAL)
                .status(EmergencyAlertStatus.PENDING)
                .latitude(36.8)
                .longitude(10.1)
                .canceledByPatient(false)
                .createdAt(LocalDateTime.now())
                .build();

        sampleClinic = Clinic.builder()
                .id(20L)
                .name("Central Clinic")
                .build();

        sampleAmbulance = Ambulance.builder()
                .id(30L)
                .clinic(sampleClinic)
                .licensePlate("ABC-123")
                .status("AVAILABLE")
                .build();
    }

    // ─── ALERTS TESTS ────────────────────────────────────────────────────────

    @Test
    public void testCreateAlert_Success() {
        EmergencyAlertRequestDTO request = EmergencyAlertRequestDTO.builder()
                .smartDeviceId(100L)
                .severity(EmergencySeverity.CRITICAL)
                .latitude(36.8)
                .longitude(10.1)
                .build();

        // Behavior
        when(smartDeviceRepository.findById(100L)).thenReturn(Optional.of(sampleDevice));
        when(alertRepository.save(any(EmergencyAlert.class))).thenAnswer(invocation -> {
            EmergencyAlert alert = invocation.getArgument(0);
            alert.setId(999L);
            return alert;
        });

        // Execution
        EmergencyAlertResponseDTO response = emergencyService.createAlert(request);

        // Verification
        assertNotNull(response);
        assertEquals(999L, response.getId());
        assertEquals("John Doe", response.getPatientName()); // Validates Mapping logic
        assertEquals(EmergencySeverity.CRITICAL, response.getSeverity());
        assertEquals(EmergencyAlertStatus.PENDING, response.getStatus()); // Automatically forced to PENDING
        
        verify(smartDeviceRepository, times(1)).findById(100L);
        verify(alertRepository, times(1)).save(any(EmergencyAlert.class));
    }

    @Test
    public void testCreateAlert_DeviceNotFound_ThrowsException() {
        EmergencyAlertRequestDTO request = EmergencyAlertRequestDTO.builder().smartDeviceId(999L).build();

        when(smartDeviceRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emergencyService.createAlert(request);
        });

        assertTrue(exception.getMessage().contains("SmartDevice not found"));
        verify(alertRepository, never()).save(any());
    }

    @Test
    public void testCancelAlertByPatient_Success() {
        when(alertRepository.findById(50L)).thenReturn(Optional.of(sampleAlert));
        when(alertRepository.save(any(EmergencyAlert.class))).thenReturn(sampleAlert);

        EmergencyAlertResponseDTO response = emergencyService.cancelAlertByPatient(50L);

        assertTrue(response.getCanceledByPatient());
        assertEquals(EmergencyAlertStatus.RESOLVED, response.getStatus());
        verify(alertRepository, times(1)).save(sampleAlert);
    }

    // ─── AMBULANCE INTERVENTION TESTS ────────────────────────────────────────

    @Test
    public void testDispatchIntervention_Success() {
        EmergencyInterventionRequestDTO request = EmergencyInterventionRequestDTO.builder()
                .emergencyAlertId(50L)
                .clinicId(20L)
                .ambulanceId(30L)
                .build();

        // Mocks for lookup
        when(alertRepository.findById(50L)).thenReturn(Optional.of(sampleAlert));
        when(clinicRepository.findById(20L)).thenReturn(Optional.of(sampleClinic));
        when(ambulanceRepository.findById(30L)).thenReturn(Optional.of(sampleAmbulance));

        // Mock for save Intervention
        when(interventionRepository.save(any(EmergencyIntervention.class))).thenAnswer(invocation -> {
            EmergencyIntervention intervention = invocation.getArgument(0);
            intervention.setId(777L);
            return intervention;
        });

        // Execution
        EmergencyInterventionResponseDTO response = emergencyService.dispatchIntervention(request);

        // Assertions logic mapping
        assertNotNull(response);
        assertEquals(777L, response.getId());
        assertEquals("John Doe", response.getPatientName());
        assertEquals(EmergencyInterventionStatus.DISPATCHED, response.getStatus());
        assertNotNull(response.getDispatchedAt());

        // Side effect: Verifies Alert status was updated to CLINIC_NOTIFIED
        assertEquals(EmergencyAlertStatus.CLINIC_NOTIFIED, sampleAlert.getStatus());
        verify(alertRepository, times(1)).save(sampleAlert);
    }

    @Test
    public void testUpdateInterventionStatus_ToArrived() {
        EmergencyIntervention intervention = EmergencyIntervention.builder()
                .id(777L)
                .status(EmergencyInterventionStatus.DISPATCHED)
                .emergencyAlert(sampleAlert)
                .clinic(sampleClinic)
                .ambulance(sampleAmbulance)
                .build();

        when(interventionRepository.findById(777L)).thenReturn(Optional.of(intervention));
        when(interventionRepository.save(any(EmergencyIntervention.class))).thenReturn(intervention);

        EmergencyInterventionResponseDTO response = emergencyService.updateInterventionStatus(777L, EmergencyInterventionStatus.ARRIVED);

        assertEquals(EmergencyInterventionStatus.ARRIVED, response.getStatus());
        assertNotNull(response.getArrivedAt()); // Custom service side-effect check
    }
}
