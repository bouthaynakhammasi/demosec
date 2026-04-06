package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.Mapper.LaboratoryMapper;
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
class LaboratoryServiceImplTest {

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @Mock
    private LaboratoryMapper laboratoryMapper;

    @InjectMocks
    private LaboratoryServiceImpl laboratoryService;

    private Laboratory laboratory;
    private LaboratoryRequest request;
    private LaboratoryResponse responseDto;

    @BeforeEach
    void setUp() {
        laboratory = new Laboratory();
        laboratory.setId(1L);
        laboratory.setName("Central Lab");
        laboratory.setActive(true);

        request = new LaboratoryRequest();
        request.setName("Central Lab");

        responseDto = new LaboratoryResponse();
        responseDto.setId(1L);
        responseDto.setName("Central Lab");
    }

    @Test
    void create_ShouldReturnLaboratoryResponse_WhenValidRequest() {
        when(laboratoryRepository.existsByName(anyString())).thenReturn(false);
        when(laboratoryMapper.toEntity(any(LaboratoryRequest.class))).thenReturn(laboratory);
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(laboratory);
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        LaboratoryResponse response = laboratoryService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(laboratoryRepository).save(any(Laboratory.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenNameExists() {
        when(laboratoryRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> laboratoryService.create(request));
        verify(laboratoryRepository, never()).save(any(Laboratory.class));
    }

    @Test
    void getById_ShouldReturnLaboratoryResponse_WhenExists() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(laboratoryMapper.toDto(laboratory)).thenReturn(responseDto);

        LaboratoryResponse response = laboratoryService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> laboratoryService.getById(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLaboratoryResponse() {
        when(laboratoryRepository.findAll()).thenReturn(Collections.singletonList(laboratory));
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        List<LaboratoryResponse> responses = laboratoryService.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void searchByName_ShouldReturnMatchingLaboratories() {
        when(laboratoryRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(laboratory));
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        List<LaboratoryResponse> responses = laboratoryService.searchByName("Central");

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void getActive_ShouldReturnActiveLaboratories() {
        when(laboratoryRepository.findByActiveTrue()).thenReturn(Collections.singletonList(laboratory));
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        List<LaboratoryResponse> responses = laboratoryService.getActive();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void update_ShouldReturnUpdatedResponse_WhenExists() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(laboratory);
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        LaboratoryResponse response = laboratoryService.update(1L, request);

        assertNotNull(response);
        verify(laboratoryMapper).updateFromDto(any(LaboratoryRequest.class), any(Laboratory.class));
        verify(laboratoryRepository).save(any(Laboratory.class));
    }

    @Test
    void toggleActive_ShouldToggleStatusAndReturnResponse() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(laboratory);
        when(laboratoryMapper.toDto(any(Laboratory.class))).thenReturn(responseDto);

        LaboratoryResponse response = laboratoryService.toggleActive(1L);

        assertNotNull(response);
        verify(laboratoryRepository).save(any(Laboratory.class));
    }

    @Test
    void delete_ShouldDeleteLaboratory_WhenExists() {
        when(laboratoryRepository.findById(1L)).thenReturn(Optional.of(laboratory));

        laboratoryService.delete(1L);

        verify(laboratoryRepository).deleteById(1L);
    }
}
