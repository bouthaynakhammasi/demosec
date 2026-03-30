package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.Mapper.LabResultMapper;
import com.aziz.demosec.repository.LabRequestRepository;
import com.aziz.demosec.repository.LabResultRepository;
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
class LabResultServiceImplTest {

    @Mock
    private LabResultRepository labResultRepository;

    @Mock
    private LabRequestRepository labRequestRepository;

    @Mock
    private LabResultMapper labResultMapper;

    @InjectMocks
    private LabResultServiceImpl labResultService;

    private LabResult result;
    private LabRequest request;
    private LabResultRequest resultRequest;

    @BeforeEach
    void setUp() {
        request = new LabRequest();
        request.setId(1L);
        request.setStatus(LabRequestStatus.PENDING);

        result = LabResult.builder()
                .id(1L)
                .labRequest(request)
                .status("COMPLETED")
                .isAbnormal(false)
                .build();

        resultRequest = new LabResultRequest();
        resultRequest.setLabRequestId(1L);
        resultRequest.setStatus("COMPLETED");
        resultRequest.setIsAbnormal(false);
    }

    @Test
    void create_ShouldReturnLabResultResponse_WhenValidRequest() {
        when(labResultRepository.existsByLabRequestId(anyLong())).thenReturn(false);
        when(labRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(labResultMapper.toEntity(any(LabResultRequest.class))).thenReturn(result);
        when(labResultRepository.save(any(LabResult.class))).thenReturn(result);
        when(labRequestRepository.save(any(LabRequest.class))).thenReturn(request);

        LabResultResponse response = labResultService.create(resultRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("COMPLETED", response.getStatus());
        verify(labResultRepository).save(any(LabResult.class));
        verify(labRequestRepository).save(any(LabRequest.class));
    }

    @Test
    void create_ShouldThrowException_WhenResultAlreadyExists() {
        when(labResultRepository.existsByLabRequestId(anyLong())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> labResultService.create(resultRequest));
        verify(labResultRepository, never()).save(any(LabResult.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenRequestDoesNotExist() {
        when(labResultRepository.existsByLabRequestId(anyLong())).thenReturn(false);
        when(labRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labResultService.create(resultRequest));
    }

    @Test
    void getById_ShouldReturnLabResultResponse_WhenExists() {
        when(labResultRepository.findById(1L)).thenReturn(Optional.of(result));

        LabResultResponse response = labResultService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(labResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> labResultService.getById(1L));
    }

    @Test
    void getAll_ShouldReturnListOfLabResultResponse() {
        when(labResultRepository.findAll()).thenReturn(Collections.singletonList(result));

        List<LabResultResponse> responses = labResultService.getAll();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void update_ShouldReturnUpdatedResponse_WhenExists() {
        when(labResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(labResultRepository.save(any(LabResult.class))).thenReturn(result);

        LabResultResponse response = labResultService.update(1L, resultRequest);

        assertNotNull(response);
        verify(labResultMapper).updateFromDto(any(LabResultRequest.class), any(LabResult.class));
        verify(labResultRepository).save(any(LabResult.class));
    }

    @Test
    void delete_ShouldDeleteResult_WhenExists() {
        when(labResultRepository.existsById(1L)).thenReturn(true);

        labResultService.delete(1L);

        verify(labResultRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(labResultRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> labResultService.delete(1L));
    }

    @Test
    void getByStatus_ShouldReturnMatchingResults() {
        when(labResultRepository.findAll()).thenReturn(Collections.singletonList(result));

        List<LabResultResponse> responses = labResultService.getByStatus("COMPLETED");

        assertFalse(responses.isEmpty());
    }

    @Test
    void verifyResult_ShouldUpdateStatusToVerified() {
        when(labResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(labResultRepository.save(any(LabResult.class))).thenAnswer(i -> i.getArgument(0));

        LabResultResponse response = labResultService.verifyResult(1L, "Dr. Smith");

        assertNotNull(response);
        assertEquals("VERIFIED", response.getStatus());
        assertEquals("Dr. Smith", response.getVerifiedBy());
    }
}
