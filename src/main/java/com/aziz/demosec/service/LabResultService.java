package com.aziz.demosec.service;

import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;

import java.util.List;

public interface LabResultService {
    LabResultResponse create(LabResultRequest request);
    LabResultResponse getById(Long id);
    LabResultResponse getByLabRequestId(Long labRequestId);
    List<LabResultResponse> getAll();
    LabResultResponse update(Long id, LabResultRequest request);
    void delete(Long id);
}