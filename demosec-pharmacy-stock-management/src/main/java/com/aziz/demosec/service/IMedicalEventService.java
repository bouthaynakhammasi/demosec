package com.aziz.demosec.service;

import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.entities.MedicalEventType;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.IOException;

public interface IMedicalEventService {
    MedicalEventResponse create(MedicalEventCreateRequest request, MultipartFile image) throws IOException;

    MedicalEventResponse update(Long id, MedicalEventUpdateRequest request, MultipartFile image) throws IOException;

    MedicalEventResponse getById(Long id);

    MedicalEventResponse getEventById(Long id);

    List<MedicalEventResponse> getAll();

    List<MedicalEventResponse> getUpcoming();

    List<MedicalEventResponse> getByType(MedicalEventType type);

    void delete(Long id);

    void participateInEvent(Long eventId, String email);

    void cancelParticipation(Long eventId, String email);

    boolean isParticipating(Long eventId, String email);

    void acceptParticipation(Long participationId);

    void rejectParticipation(Long participationId);
}