package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Diagnosis;
import com.aziz.demosec.Mapper.DiagnosisMapper;
import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.DiagnosisRepository;
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
class DiagnosisServiceTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private DiagnosisMapper diagnosisMapper;

    @InjectMocks
    private DiagnosisService diagnosisService;

    private Consultation consultation;
    private Diagnosis diagnosis;
    private DiagnosisRequest request;
    private DiagnosisResponse response;

    @BeforeEach
    void setUp() {
        consultation = TestDataFactory.createConsultation(1L, null, null);
        diagnosis = TestDataFactory.createDiagnosis(1L, consultation);
        
        request = DiagnosisRequest.builder()
                .consultationId(1L)
                .description("Test Diagnosis")
                .type("PRIMARY")
                .build();
        
        response = DiagnosisResponse.builder()
                .id(1L)
                .description("Test Diagnosis")
                .build();
    }

    @Test
    void addDiagnosis_ShouldReturnResponse_WhenValid() {
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(diagnosis);
        when(diagnosisMapper.toDto(any(Diagnosis.class))).thenReturn(response);

        DiagnosisResponse result = diagnosisService.addDiagnosis(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(diagnosisRepository).save(any(Diagnosis.class));
    }

    @Test
    void selectDiagnosisById_ShouldReturnResponse() {
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(diagnosis));
        when(diagnosisMapper.toDto(any(Diagnosis.class))).thenReturn(response);

        DiagnosisResponse result = diagnosisService.selectDiagnosisByIdWithOrElse(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteDiagnosisById_ShouldCallRepository() {
        diagnosisService.deleteDiagnosisById(1L);
        verify(diagnosisRepository).deleteById(1L);
    }
}
