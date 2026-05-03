package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.Entities.Treatment;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Mapper.TreatmentMapper;
import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.TreatmentRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreatmentServiceTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private TreatmentMapper treatmentMapper;

    @InjectMocks
    private TreatmentService treatmentService;

    private MedicalRecord medicalRecord;
    private User doctor;
    private Consultation consultation;
    private Treatment treatment;
    private TreatmentRequest request;
    private TreatmentResponse response;

    @BeforeEach
    void setUp() {
        medicalRecord = TestDataFactory.createMedicalRecord(1L, null);
        doctor = TestDataFactory.createDoctor(2L, "doctor@test.com");
        consultation = TestDataFactory.createConsultation(1L, medicalRecord, doctor);
        treatment = TestDataFactory.createTreatment(1L, consultation);
        
        request = TreatmentRequest.builder()
                .consultationId(1L)
                .treatmentType("MEDICATION")
                .description("Test Treatment")
                .startDate(LocalDate.now().toString())
                .status("ONGOING")
                .build();
        
        response = TreatmentResponse.builder()
                .id(1L)
                .description("Test Treatment")
                .build();
    }

    @Test
    void addTreatment_ShouldReturnResponse_WhenValid() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
        when(treatmentRepository.save(any(Treatment.class))).thenReturn(treatment);
        when(treatmentMapper.toDto(any(Treatment.class))).thenReturn(response);

        TreatmentResponse result = treatmentService.addTreatment(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(treatmentRepository).save(any(Treatment.class));
    }

    @Test
    void selectTreatmentById_ShouldReturnResponse() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(treatmentMapper.toDto(any(Treatment.class))).thenReturn(response);

        TreatmentResponse result = treatmentService.selectTreatmentByIdWithOrElse(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteTreatmentById_ShouldCallRepository() {
        treatmentService.deleteTreatmentById(1L);
        verify(treatmentRepository).deleteById(1L);
    }
}
