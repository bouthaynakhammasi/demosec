package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;

import java.util.List;

public interface LabRequestService {
    LabRequestResponse create(LabRequestRequest request);
    LabRequestResponse getById(Long id);
    List<LabRequestResponse> getAll();
    List<LabRequestResponse> getByPatient(Long patientId);
    List<LabRequestResponse> getByDoctor(Long doctorId);
    List<LabRequestResponse> getByLaboratory(Long laboratoryId);
    LabRequestResponse updateStatus(Long id, LabRequestStatus status);
    void delete(Long id);
}