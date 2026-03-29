package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LabTest;
import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.LabTestMapper;
import com.aziz.demosec.repository.LabTestRepository;
import com.aziz.demosec.repository.LaboratoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabTestServiceImplTest {

    @Mock
    private LabTestRepository labTestRepository;

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @Mock
    private LabTestMapper labTestMapper;

    @InjectMocks
    private LabTestServiceImpl labTestService;

    private LabTest labTest;
    private Laboratory laboratory;
    private LabTestRequest request;
    private LabTestResponse responseDto;

    @BeforeEach
    void setUp() {
        laboratory = new Laboratory();
        laboratory.setId(1L);

        labTest = LabTest.builder()
                .id(1L)
                .name("Blood Test")
                .laboratory(laboratory)
                .build();

        request = new LabTestRequest();
        request.setLaboratoryId(1L);
        request.setName("Blood Test");

        responseDto = LabTestResponse.builder()
                .id(1L)
                .name("Blood Test")
                .laboratoryId(1L)
                .build();
    }

    @Test
    void create_ShouldReturnLabTestResponse_WhenRequestIsValid() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(labTestRepository.existsByNameAndLaboratoryId(anyString(), anyLong())).thenReturn(false);
        when(labTestMapper.toEntity(any(LabTestRequest.class))).thenReturn(labTest);
        when(labTestRepository.save(any(LabTest.class))).thenReturn(labTest);
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(responseDto);

        LabTestResponse response = labTestService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Blood Test", response.getName());
        verify(labTestRepository).save(any(LabTest.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenLaboratoryDoesNotExist() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labTestService.create(request));
        verify(labTestRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenTestAlreadyExists() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(labTestRepository.existsByNameAndLaboratoryId(anyString(), anyLong())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> labTestService.create(request));
        verify(labTestRepository, never()).save(any());
    }

    @Test
    void getById_ShouldReturnLabTestResponse_WhenExists() {
        when(labTestRepository.findById(1L)).thenReturn(Optional.of(labTest));
        when(labTestMapper.toDto(labTest)).thenReturn(responseDto);

        LabTestResponse response = labTestService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(labTestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labTestService.getById(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLabTestResponse() {
        when(labTestRepository.findAll()).thenReturn(Collections.singletonList(labTest));
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(responseDto);

        List<LabTestResponse> responses = labTestService.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void getByLaboratory_ShouldReturnMatchingTests() {
        when(labTestRepository.findByLaboratoryId(1L)).thenReturn(Collections.singletonList(labTest));
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(responseDto);

        List<LabTestResponse> responses = labTestService.getByLaboratory(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void update_ShouldReturnUpdatedResponse_WhenExists() {
        when(labTestRepository.findById(1L)).thenReturn(Optional.of(labTest));
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(labTestRepository.save(any(LabTest.class))).thenReturn(labTest);
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(responseDto);

        LabTestResponse response = labTestService.update(1L, request);

        assertNotNull(response);
        verify(labTestMapper).updateFromDto(any(LabTestRequest.class), any(LabTest.class));
        verify(labTestRepository).save(any(LabTest.class));
    }

    @Test
    void delete_ShouldDeleteTest_WhenExists() {
        when(labTestRepository.findById(1L)).thenReturn(Optional.of(labTest));

        labTestService.delete(1L);

        verify(labTestRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(labTestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labTestService.delete(1L));
    }
}
