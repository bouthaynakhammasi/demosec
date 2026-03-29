package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.entities.*;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalEventServiceImpl implements IMedicalEventService {

    private final MedicalEventRepository medicalEventRepository;
    private final UserRepository userRepository;

    @Override
    public MedicalEventResponse create(MedicalEventCreateRequest request) {

        User creator = null;
        if (request.getCreatedById() != null) {
            creator = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getCreatedById()));
        }

        MedicalEvent event;

        if (request.getEventType() == MedicalEventType.ONLINE) {
            event = OnlineEvent.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .date(request.getDate())
                    .eventType(MedicalEventType.ONLINE)
                    .createdBy(creator)
                    .platformName(request.getPlatformName())
                    .meetingLink(request.getMeetingLink())
                    .meetingPassword(request.getMeetingPassword())
                    .build();
        } else if (request.getEventType() == MedicalEventType.PHYSICAL) {
            event = PhysicalEvent.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .date(request.getDate())
                    .eventType(MedicalEventType.PHYSICAL)
                    .createdBy(creator)
                    .venueName(request.getVenueName())
                    .address(request.getAddress())
                    .city(request.getCity())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .capacity(request.getCapacity())
                    .build();
        } else {
            throw new IllegalArgumentException("eventType is required (ONLINE or PHYSICAL)");
        }

        return toResponse(medicalEventRepository.save(event));
    }

    @Override
    public MedicalEventResponse update(Long id, MedicalEventUpdateRequest request) {
        MedicalEvent e = medicalEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MedicalEvent not found: " + id));

        if (request.getTitle() != null) e.setTitle(request.getTitle());
        if (request.getDescription() != null) e.setDescription(request.getDescription());
        if (request.getDate() != null) e.setDate(request.getDate());

        if (e instanceof OnlineEvent oe) {
            if (request.getPlatformName() != null) oe.setPlatformName(request.getPlatformName());
            if (request.getMeetingLink() != null) oe.setMeetingLink(request.getMeetingLink());
            if (request.getMeetingPassword() != null) oe.setMeetingPassword(request.getMeetingPassword());
        }

        if (e instanceof PhysicalEvent pe) {
            if (request.getVenueName() != null) pe.setVenueName(request.getVenueName());
            if (request.getAddress() != null) pe.setAddress(request.getAddress());
            if (request.getCity() != null) pe.setCity(request.getCity());
            if (request.getPostalCode() != null) pe.setPostalCode(request.getPostalCode());
            if (request.getCountry() != null) pe.setCountry(request.getCountry());
            if (request.getCapacity() != null) pe.setCapacity(request.getCapacity());
        }

        return toResponse(medicalEventRepository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalEventResponse getById(Long id) {
        return toResponse(medicalEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MedicalEvent not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalEventResponse> getAll() {
        return medicalEventRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalEventResponse> getUpcoming() {
        return medicalEventRepository.findByDateAfterOrderByDateAsc(LocalDateTime.now())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalEventResponse> getByType(MedicalEventType type) {
        return medicalEventRepository.findByEventType(type)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        if (!medicalEventRepository.existsById(id)) {
            throw new EntityNotFoundException("MedicalEvent not found: " + id);
        }
        medicalEventRepository.deleteById(id);
    }

    private MedicalEventResponse toResponse(MedicalEvent e) {
        MedicalEventResponse.MedicalEventResponseBuilder b = MedicalEventResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .date(e.getDate())
                .eventType(e.getEventType())
                .createdById(e.getCreatedBy() != null ? e.getCreatedBy().getId() : null);

        if (e instanceof OnlineEvent oe) {
            b.platformName(oe.getPlatformName())
                    .meetingLink(oe.getMeetingLink())
                    .meetingPassword(oe.getMeetingPassword());
        }
        if (e instanceof PhysicalEvent pe) {
            b.venueName(pe.getVenueName())
                    .address(pe.getAddress())
                    .city(pe.getCity())
                    .postalCode(pe.getPostalCode())
                    .country(pe.getCountry())
                    .capacity(pe.getCapacity());
        }
        return b.build();
    }
}