package com.aziz.demosec.service;

import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Mapper.MedicalRecordMapper;
import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Patient patient;
    private MedicalRecord record;
    private MedicalRecordRequest request;
    private MedicalRecordResponse response;

    @BeforeEach
    void setUp() {
        patient = TestDataFactory.createPatient(1L, "patient@test.com");
        record = TestDataFactory.createMedicalRecord(1L, patient);
        
        request = MedicalRecordRequest.builder()
                .patientId(1L)
                .build();
        
        response = MedicalRecordResponse.builder()
                .id(1L)
                .patientId(1L)
                .build();
    }

    @Test
    void addMedicalRecord_ShouldReturnResponse_WhenValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(record);
        when(medicalRecordMapper.toDto(any(MedicalRecord.class))).thenReturn(response);

        MedicalRecordResponse result = medicalRecordService.addMedicalRecord(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void selectMedicalRecordByPatientId_ShouldReturnResponse() {
        when(medicalRecordRepository.findByPatientId(1L)).thenReturn(Optional.of(record));
        when(medicalRecordMapper.toDto(any(MedicalRecord.class))).thenReturn(response);

        MedicalRecordResponse result = medicalRecordService.selectMedicalRecordByPatientId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void selectAllMedicalRecords_ShouldReturnList() {
        when(medicalRecordRepository.findAll()).thenReturn(List.of(record));
        when(medicalRecordMapper.toDto(any(MedicalRecord.class))).thenReturn(response);

        List<MedicalRecordResponse> results = medicalRecordService.selectAllMedicalRecords();

        assertThat(results).hasSize(1);
    }

    @Test
    void deleteMedicalRecordById_ShouldCallRepository() {
        medicalRecordService.deleteMedicalRecordById(1L);
        verify(medicalRecordRepository).deleteById(1L);
    }
}
