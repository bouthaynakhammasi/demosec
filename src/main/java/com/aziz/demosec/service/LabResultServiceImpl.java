package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.LabResultMapper;
import com.aziz.demosec.repository.LabRequestRepository;
import com.aziz.demosec.repository.LabResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabResultServiceImpl implements LabResultService {

    private final LabResultRepository labResultRepository;
    private final LabRequestRepository labRequestRepository;
    private final LabResultMapper labResultMapper;

    @Override
    public LabResultResponse create(LabResultRequest request) {
        if (labResultRepository.existsByLabRequestId(request.getLabRequestId())) {
            throw new IllegalStateException("A result already exists for this lab request.");
        }

        LabRequest labRequest = labRequestRepository.findById(request.getLabRequestId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabRequest not found with id: " + request.getLabRequestId()));

        LabResult result = labResultMapper.toEntity(request);
        result.setLabRequest(labRequest);
        result.setCompletedAt(LocalDateTime.now());

        labRequest.setStatus(LabRequestStatus.COMPLETED);
        labRequestRepository.save(labRequest);

        return labResultMapper.toDto(labResultRepository.save(result));
    }

    @Override
    public LabResultResponse getById(Long id) {
        LabResult result = labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found with id: " + id));
        return labResultMapper.toDto(result);
    }

    @Override
    public LabResultResponse getByLabRequestId(Long labRequestId) {
        LabResult result = labResultRepository.findByLabRequestId(labRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found for labRequest id: " + labRequestId));
        return labResultMapper.toDto(result);
    }

    @Override
    public List<LabResultResponse> getAll() {
        return labResultRepository.findAll()
                .stream()
                .map(labResultMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabResultResponse update(Long id, LabResultRequest request) {
        LabResult result = labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found with id: " + id));

        labResultMapper.updateFromDto(request, result);
        result.setCompletedAt(LocalDateTime.now());

        return labResultMapper.toDto(labResultRepository.save(result));
    }

    @Override
    public void delete(Long id) {
        if (!labResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("LabResult not found with id: " + id);
        }
        labResultRepository.deleteById(id);
    }
}