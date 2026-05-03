package com.aziz.demosec.service;

import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;

import java.util.List;

public interface IProgressTrackingService {
    ProgressTrackingResponse addTracking(ProgressTrackingRequest request);
    ProgressTrackingResponse getTrackingById(Long id);
    List<ProgressTrackingResponse> getAllTrackings();
    List<ProgressTrackingResponse> getTrackingsByPatientId(Long patientId);
    ProgressTrackingResponse updateTracking(Long id, ProgressTrackingRequest request);
    void deleteTracking(Long id);
}
