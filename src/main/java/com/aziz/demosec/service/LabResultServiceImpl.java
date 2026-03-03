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
import java.util.ArrayList;
import java.util.List;

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
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            responses.add(labResultMapper.toDto(result));
        }
        return responses;
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

    // Méthodes innovantes
    @Override
    public List<LabResultResponse> getByStatus(String status) {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (status.equals(result.getStatus())) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> getByPriority(Integer priority) {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (priority.equals(result.getPriority())) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> getUrgentResults() {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (Boolean.TRUE.equals(result.getIsUrgent())) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> getAbnormalResults() {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (Boolean.TRUE.equals(result.getIsAbnormal())) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> getByTechnicianName(String technicianName) {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (technicianName != null && technicianName.equals(result.getTechnicianName())) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (result.getCompletedAt() != null && 
                result.getCompletedAt().isAfter(startDate) && 
                result.getCompletedAt().isBefore(endDate)) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public List<LabResultResponse> searchByKeyword(String keyword) {
        List<LabResultResponse> responses = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if ((result.getResultData() != null && result.getResultData().toLowerCase().contains(keyword.toLowerCase())) ||
                (result.getResultFile() != null && result.getResultFile().toLowerCase().contains(keyword.toLowerCase())) ||
                (result.getTestType() != null && result.getTestType().toLowerCase().contains(keyword.toLowerCase()))) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public LabResultResponse verifyResult(Long id, String verifiedBy) {
        LabResult result = findOrThrow(id);
        result.setVerifiedBy(verifiedBy);
        result.setVerifiedAt(LocalDateTime.now());
        result.setStatus("VERIFIED");
        return labResultMapper.toDto(labResultRepository.save(result));
    }

    @Override
    public LabResultResponse markAsUrgent(Long id) {
        LabResult result = findOrThrow(id);
        result.setIsUrgent(true);
        result.setPriority(1);
        return LabResultMapper.toDto(labResultRepository.save(result));
    }

    @Override
    public LabResultResponse updateStatus(Long id, String status) {
        LabResult result = findOrThrow(id);
        result.setStatus(status);
        return LabResultMapper.toDto(labResultRepository.save(result));
    }

    @Override
    public Long getTotalResults() {
        return (long) labResultRepository.findAll().size();
    }

    @Override
    public Long getResultsByStatus(String status) {
        long count = 0;
        for (LabResult result : labResultRepository.findAll()) {
            if (status.equals(result.getStatus())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<String> getAvailableTechnicians() {
        List<String> technicians = new ArrayList<>();
        for (LabResult result : labResultRepository.findAll()) {
            if (result.getTechnicianName() != null && !technicians.contains(result.getTechnicianName())) {
                technicians.add(result.getTechnicianName());
            }
        }
        return technicians;
    }

    @Override
    public List<LabResultResponse> getRecentResults(int days) {
        List<LabResultResponse> responses = new ArrayList<>();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        for (LabResult result : labResultRepository.findAll()) {
            if (result.getCompletedAt() != null && result.getCompletedAt().isAfter(cutoffDate)) {
                responses.add(labResultMapper.toDto(result));
            }
        }
        return responses;
    }

    @Override
    public LabResultResponse duplicateResult(Long id) {
        LabResult original = findOrThrow(id);
        LabResult duplicate = LabResult.builder()
                .labRequest(original.getLabRequest())
                .resultFile(original.getResultFile())
                .resultData(original.getResultData())
                .testType(original.getTestType())
                .normalValue(original.getNormalValue())
                .abnormalFindings(original.getAbnormalFindings())
                .technicianName(original.getTechnicianName())
                .verifiedBy(original.getVerifiedBy())
                .isAbnormal(original.getIsAbnormal())
                .recommendations(original.getRecommendations())
                .priority(original.getPriority())
                .completedAt(LocalDateTime.now())
                .status("DUPLICATE")
                .build();
        return labResultMapper.toDto(labResultRepository.save(duplicate));
    }

    private LabResult findOrThrow(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabResult not found with id: " + id));
    }
}