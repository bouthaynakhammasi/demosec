package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClinicServiceImplTest {

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    private Clinic sampleClinic;

    @BeforeEach
    public void setup() {
        sampleClinic = new Clinic();
        sampleClinic.setId(1L);
        sampleClinic.setName("MedCenter");
        sampleClinic.setAddress("123 Main St");
        sampleClinic.setLatitude(36.8);
        sampleClinic.setLongitude(10.1);
        sampleClinic.setHasEmergency(false); // Will be testing auto-toggle logic
        sampleClinic.setHasAmbulance(false);
    }

    @Test
    public void testCreateClinic_AutoSetsMandatoryFields() {
        // Assert initial state before logic runs
        assertFalse(sampleClinic.isHasEmergency());
        assertFalse(sampleClinic.isHasAmbulance());

        when(clinicRepository.save(any(Clinic.class))).thenReturn(sampleClinic);

        Clinic saved = clinicService.createClinic(sampleClinic);

        // Verification of business rule: "Initialiser les champs obligatoires si non fournis"
        assertTrue(saved.isHasEmergency());
        assertTrue(saved.isHasAmbulance());
        verify(clinicRepository, times(1)).save(sampleClinic);
    }

    @Test
    public void testUpdateClinic_Success() {
        Clinic clinicDetails = new Clinic();
        clinicDetails.setName("Updated Name");
        clinicDetails.setAddress("New Address");
        clinicDetails.setLatitude(37.0);
        clinicDetails.setLongitude(11.0);
        clinicDetails.setHasEmergency(true);
        clinicDetails.setHasAmbulance(true);

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(sampleClinic));
        when(clinicRepository.save(any(Clinic.class))).thenReturn(sampleClinic);

        Clinic updated = clinicService.updateClinic(1L, clinicDetails);

        assertEquals("Updated Name", updated.getName());
        assertEquals("New Address", updated.getAddress());
        assertEquals(37.0, updated.getLatitude());
        assertEquals(11.0, updated.getLongitude());
        verify(clinicRepository, times(1)).save(sampleClinic);
    }

    @Test
    public void testUpdateClinic_NotFound() {
        when(clinicRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            clinicService.updateClinic(99L, sampleClinic);
        });

        assertTrue(error.getMessage().contains("Clinic not found"));
        verify(clinicRepository, never()).save(any());
    }
}
