package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.entities.*;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.repository.EventParticipationRepository;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.domain.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalEventServiceImpl implements IMedicalEventService {

    private final MedicalEventRepository medicalEventRepository;
    private final UserRepository userRepository;
    private final EventParticipationRepository participationRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public MedicalEventResponse create(MedicalEventCreateRequest request, MultipartFile image) throws IOException {

        User creator = null;
        if (request.getCreatedById() != null) {
            creator = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getCreatedById()));
        }

        String imageUrl = request.getImageUrl();
        if (image != null && !image.isEmpty()) {
            imageUrl = "data:" + image.getContentType() + ";base64,"
                    + Base64.getEncoder().encodeToString(image.getBytes());
        }

        MedicalEvent event;

        LocalDateTime eventDate = request.getDate() != null ? request.getDate().atStartOfDay() : null;

        if (request.getEventType() == MedicalEventType.ONLINE) {
            event = OnlineEvent.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .date(eventDate)
                    .eventType(MedicalEventType.ONLINE)
                    .createdBy(creator)
                    .imageUrl(imageUrl)
                    .platformName(request.getPlatformName())
                    .meetingLink(request.getMeetingLink())
                    .meetingPassword(request.getMeetingPassword())
                    .build();
        } else if (request.getEventType() == MedicalEventType.PHYSICAL) {
            event = PhysicalEvent.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .date(eventDate)
                    .eventType(MedicalEventType.PHYSICAL)
                    .createdBy(creator)
                    .imageUrl(imageUrl)
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
    public MedicalEventResponse update(Long id, MedicalEventUpdateRequest request, MultipartFile image)
            throws IOException {
        MedicalEvent e = medicalEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MedicalEvent not found: " + id));

        if (request.getTitle() != null)
            e.setTitle(request.getTitle());
        if (request.getDescription() != null)
            e.setDescription(request.getDescription());
        if (request.getDate() != null)
            e.setDate(request.getDate().atStartOfDay());

        if (image != null && !image.isEmpty()) {
            String b64 = "data:" + image.getContentType() + ";base64,"
                    + Base64.getEncoder().encodeToString(image.getBytes());
            e.setImageUrl(b64);
        } else if (request.getImageUrl() != null) {
            e.setImageUrl(request.getImageUrl());
        }

        if (e instanceof OnlineEvent oe) {
            if (request.getPlatformName() != null)
                oe.setPlatformName(request.getPlatformName());
            if (request.getMeetingLink() != null)
                oe.setMeetingLink(request.getMeetingLink());
            if (request.getMeetingPassword() != null)
                oe.setMeetingPassword(request.getMeetingPassword());
        }

        if (e instanceof PhysicalEvent pe) {
            if (request.getVenueName() != null)
                pe.setVenueName(request.getVenueName());
            if (request.getAddress() != null)
                pe.setAddress(request.getAddress());
            if (request.getCity() != null)
                pe.setCity(request.getCity());
            if (request.getPostalCode() != null)
                pe.setPostalCode(request.getPostalCode());
            if (request.getCountry() != null)
                pe.setCountry(request.getCountry());
            if (request.getCapacity() != null)
                pe.setCapacity(request.getCapacity());
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
                .createdById(e.getCreatedBy() != null ? e.getCreatedBy().getId() : null)
                .imageUrl(e.getImageUrl());

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

    @Override
    @Transactional(readOnly = true)
    public MedicalEventResponse getEventById(Long id) {
        return getById(id);
    }

    @Override
    public void participateInEvent(Long eventId, String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        MedicalEvent event = medicalEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalEvent not found: " + eventId));

        boolean alreadyJoined = participationRepository
                .existsByEventIdAndPatientId(event.getId(), patient.getId());

        if (alreadyJoined) {
            throw new RuntimeException("Already registered for this event");
        }

        EventParticipation p = EventParticipation.builder()
                .event(event)
                .patient(patient)
                .registeredAt(LocalDateTime.now())
                .status("CONFIRMED")
                .build();

        p.setStatus("PENDING");
        EventParticipation saved = participationRepository.save(p);

        // Notify All Admins
        userRepository.findByRole(Role.ADMIN).forEach(admin -> {
            Notification notification = Notification.builder()
                    .recipient(admin)
                    .sender(patient)
                    .message(patient.getFullName() + " joined your event: \"" + event.getTitle() + "\"")
                    .type("EVENT_JOIN")
                    .targetId(eventId)
                    .participationId(saved.getId())
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        });
    }

    @Override
    public void acceptParticipation(Long participationId) {
        EventParticipation p = participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        p.setStatus("CONFIRMED");
        participationRepository.save(p);

        // Notify Patient
        User admin = userRepository.findByRole(Role.ADMIN).stream().findFirst().orElse(null);
        Notification notif = Notification.builder()
                .recipient(p.getPatient())
                .sender(admin)
                .message("✅ Your participation in \"" + p.getEvent().getTitle() + "\" has been ACCEPTED! Your ticket is ready.")
                .type("PARTICIPATION_ACCEPTED")
                .targetId(p.getEvent().getId())
                .participationId(participationId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notif);
    }

    @Override
    public void rejectParticipation(Long participationId) {
        EventParticipation p = participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        p.setStatus("REJECTED");
        participationRepository.save(p);

        // Notify Patient
        Notification notif = Notification.builder()
                .recipient(p.getPatient())
                .message("❌ Your participation in \"" + p.getEvent().getTitle() + "\" has been rejected.")
                .type("PARTICIPATION_REJECTED")
                .targetId(p.getEvent().getId())
                .participationId(participationId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notif);
    }

    @Override
    public void cancelParticipation(Long eventId, String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        participationRepository.deleteByEventIdAndPatientId(eventId, patient.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isParticipating(Long eventId, String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return participationRepository.existsByEventIdAndPatientId(eventId, patient.getId());
    }
}