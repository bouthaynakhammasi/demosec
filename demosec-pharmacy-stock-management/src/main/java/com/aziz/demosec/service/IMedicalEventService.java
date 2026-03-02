package com.aziz.demosec.service;

import com.aziz.demosec.dto.request.MedicalEventCreateRequest;
import com.aziz.demosec.dto.request.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.response.MedicalEventResponse;
import com.aziz.demosec.entities.MedicalEventType;

import java.util.List;

public interface IMedicalEventService {
    MedicalEventResponse create(MedicalEventCreateRequest request);
    MedicalEventResponse update(Long id, MedicalEventUpdateRequest request);
    MedicalEventResponse getById(Long id);
    List<MedicalEventResponse> getAll();
    List<MedicalEventResponse> getUpcoming();
    List<MedicalEventResponse> getByType(MedicalEventType type);
    void delete(Long id);
}