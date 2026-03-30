package com.aziz.demosec.service;

import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;

import java.util.List;

public interface LabTestService {
    LabTestResponse create(LabTestRequest request);
    LabTestResponse getById(Long id);
    List<LabTestResponse> getAll();
    List<LabTestResponse> getByLaboratory(Long laboratoryId);
    LabTestResponse update(Long id, LabTestRequest request);
    void delete(Long id);
}