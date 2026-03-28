package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Mapper.ConsultationMapper;
import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsultationMapper consultationMapper;

    @InjectMocks
    private ConsultationService consultationService;

    private MedicalRecord medicalRecord;
    private User doctor;
    private Consultation consultation;
    private ConsultationRequest request;
    private ConsultationResponse response;

    @BeforeEach
    void setUp() {
        medicalRecord = TestDataFactory.createMedicalRecord(1L, null);
        doctor = TestDataFactory.createDoctor(2L, "doctor@test.com");
        consultation = TestDataFactory.createConsultation(1L, medicalRecord, doctor);
        
        request = ConsultationRequest.builder()
                .medicalRecordId(1L)
                .doctorId(2L)
                .date(LocalDateTime.now())
                .observations("Typical symptoms")
                .notes("Test Notes")
                .build();
        
        response = ConsultationResponse.builder()
                .id(1L)
                .observations("Typical symptoms")
                .build();
    }

    @Test
    void addConsultation_ShouldReturnResponse_WhenValid() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(medicalRecord));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);
        when(consultationMapper.toDto(any(Consultation.class))).thenReturn(response);

        ConsultationResponse result = consultationService.addConsultation(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void selectConsultationById_ShouldReturnResponse() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toDto(any(Consultation.class))).thenReturn(response);

        ConsultationResponse result = consultationService.selectConsultationByIdWithOrElse(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteConsultationById_ShouldCallRepository() {
        consultationService.deleteConsultationById(1L);
        verify(consultationRepository).deleteById(1L);
    }
}
