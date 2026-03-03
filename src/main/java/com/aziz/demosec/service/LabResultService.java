package com.aziz.demosec.service;

import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface LabResultService {
    LabResultResponse create(LabResultRequest request);
    LabResultResponse getById(Long id);
    LabResultResponse getByLabRequestId(Long labRequestId);
    List<LabResultResponse> getAll();
    LabResultResponse update(Long id, LabResultRequest request);
    void delete(Long id);
    
    // Méthodes innovantes
    List<LabResultResponse> getByStatus(String status);
    List<LabResultResponse> getByPriority(Integer priority);
    List<LabResultResponse> getUrgentResults();
    List<LabResultResponse> getAbnormalResults();
    List<LabResultResponse> getByTechnicianName(String technicianName);
    List<LabResultResponse> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<LabResultResponse> searchByKeyword(String keyword);
    LabResultResponse verifyResult(Long id, String verifiedBy);
    LabResultResponse markAsUrgent(Long id);
    LabResultResponse updateStatus(Long id, String status);
    Long getTotalResults();
    Long getResultsByStatus(String status);
    List<String> getAvailableTechnicians();
    List<LabResultResponse> getRecentResults(int days);
    LabResultResponse duplicateResult(Long id);
}