package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Mapper.PrescriptionMapper;
import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.PrescriptionRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Consultation consultation;
    private Prescription prescription;
    private PrescriptionRequest request;
    private PrescriptionResponse response;

    @BeforeEach
    void setUp() {
        consultation = TestDataFactory.createConsultation(1L, null, null);
        prescription = TestDataFactory.createPrescription(1L, consultation);
        
        request = PrescriptionRequest.builder()
                .consultationId(1L)
                .date("2026-03-28")
                .medication("Paracetamol")
                .dosage("500mg")
                .instructions("3 times a day")
                .build();
        
        response = PrescriptionResponse.builder()
                .id(1L)
                .build();
    }

    @Test
    void addPrescription_ShouldReturnResponse_WhenValid() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);
        when(prescriptionMapper.toDto(any(Prescription.class))).thenReturn(response);

        PrescriptionResponse result = prescriptionService.addPrescription(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void selectPrescriptionById_ShouldReturnResponse() {
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        when(prescriptionMapper.toDto(any(Prescription.class))).thenReturn(response);

        PrescriptionResponse result = prescriptionService.selectPrescriptionByIdWithOrElse(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deletePrescriptionById_ShouldCallRepository() {
        prescriptionService.deletePrescriptionById(1L);
        verify(prescriptionRepository).deleteById(1L);
    }
}
