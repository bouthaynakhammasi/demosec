package com.aziz.demosec.service;

import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.Entities.MedicalEventType;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IMedicalEventService {
    String storeImage(MultipartFile image);
    MedicalEventResponse create(MedicalEventCreateRequest request);
    MedicalEventResponse update(Long id, MedicalEventUpdateRequest request);
    MedicalEventResponse getById(Long id);
    List<MedicalEventResponse> getAll();
    List<MedicalEventResponse> getUpcoming();
    List<MedicalEventResponse> getByType(MedicalEventType type);
    void delete(Long id);
}