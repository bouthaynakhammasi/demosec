package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.MedicalEventDTO;
import com.aziz.demosec.repository.EventRegistrationRepository;
import com.aziz.demosec.repository.EventSeatRepository;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalEventServiceImpl implements MedicalEventService {

    private final MedicalEventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventSeatRepository seatRepository;
    private final EventRegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    private final String uploadDir = "uploads/";

    @Override
    public List<MedicalEventDTO> getAllEvents() {
        return eventRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public MedicalEventDTO getEventById(Long id) {
        return eventRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public MedicalEventDTO createEvent(MedicalEventDTO dto, MultipartFile image) {
        MedicalEvent event = mapToEntity(dto);
        if (image != null && !image.isEmpty())
            event.setImageUrl(saveImage(image));
        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            event.setCreatedBy(user);
        }
        MedicalEvent saved = eventRepository.save(event);
        return mapToDTO(saved);
    }

    @Override
    public MedicalEventDTO updateEvent(Long id, MedicalEventDTO dto, MultipartFile image) {
        MedicalEvent existing = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        if (image != null && !image.isEmpty())
            existing.setImageUrl(saveImage(image));
        MedicalEvent saved = eventRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventSeat> getEventSeats(Long eventId) {
        return seatRepository.findByEventId(eventId);
    }

    @Override
    @Transactional
    public void generateLayout(Long eventId, String venueType) {
        MedicalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        seatRepository.deleteByEventId(eventId);
        List<EventSeat> seats = new ArrayList<>();
        int rows = 10, cols = 15;
        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= cols; s++) {
                seats.add(EventSeat.builder()
                        .event(event).zoneName("Main").seatLabel("R" + r + "-S" + s)
                        .rowNumber(r).seatNumber(s).posX(s * 50).posY(r * 60)
                        .status(SeatStatus.AVAILABLE).build());
            }
        }
        seatRepository.saveAll(seats);
    }

    @Override
    public void saveSeatsBatch(Long eventId, List<EventSeat> seats) {
        MedicalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        seats.forEach(s -> s.setEvent(event));
        seatRepository.saveAll(seats);
    }

    @Override
    public void reserveSeat(Long seatId) {
        EventSeat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setStatus(SeatStatus.RESERVED);
        seatRepository.save(seat);
    }

    @Override
    public void releaseSeat(Long seatId) {
        EventSeat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }

    @Override
    public Map<String, Object> getEventAnalytics(Long eventId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("eventId", eventId);
        analytics.put("totalRegistrations", registrationRepository.countByEventId(eventId));
        return analytics;
    }

    @Override
    @Transactional
    public void participateInEvent(Long eventId, Long userId) {
        MedicalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (registrationRepository.findByEventIdAndParticipantId(eventId, userId).isPresent()) {
            throw new RuntimeException("Already registered");
        }

        EventRegistration reg = EventRegistration.builder()
                .event(event).participant(user)
                .status(RegistrationStatus.PENDING) // Start as PENDING
                .createdAt(LocalDateTime.now()).build();
        EventRegistration saved = registrationRepository.save(reg);

        // Notify Admins
        notificationService.notifyAdminOfEventJoin(saved);
    }

    @Override
    public void cancelParticipation(Long eventId, Long userId) {
        EventRegistration reg = registrationRepository.findByEventIdAndParticipantId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        registrationRepository.delete(reg);
    }

    @Override
    public Map<String, Object> isParticipating(Long eventId, Long userId) {
        Optional<EventRegistration> reg = registrationRepository.findByEventIdAndParticipantId(eventId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("participating", reg.isPresent());
        if (reg.isPresent()) {
            result.put("status", reg.get().getStatus());
            result.put("participationId", reg.get().getId());
        }
        return result;
    }

    @Override
    @Transactional
    public void acceptParticipation(Long participationId) {
        EventRegistration reg = registrationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(RegistrationStatus.REGISTERED);
        registrationRepository.save(reg);

        // Notify Patient
        notificationService.notifyPatientOfParticipationUpdate(reg);
    }

    @Override
    @Transactional
    public void rejectParticipation(Long participationId) {
        EventRegistration reg = registrationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(RegistrationStatus.REJECTED);
        registrationRepository.save(reg);

        // Notify Patient
        notificationService.notifyPatientOfParticipationUpdate(reg);
    }

    @Override
    public byte[] generateTicketPdf(Long participationId) {
        // Dummy PDF content
        return "Dummy PDF Ticket Content".getBytes();
    }

    private String saveImage(MultipartFile image) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath))
                Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Files.copy(image.getInputStream(), uploadPath.resolve(filename));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("File error", e);
        }
    }

    private MedicalEventDTO mapToDTO(MedicalEvent event) {
        return MedicalEventDTO.builder()
                .id(event.getId()).title(event.getTitle()).description(event.getDescription())
                .date(event.getDate()).eventType(event.getEventType()).imageUrl(event.getImageUrl())
                .ticketPrice(event.getTicketPrice()).build();
    }

    private MedicalEvent mapToEntity(MedicalEventDTO dto) {
        if (dto.getEventType() == MedicalEventType.PHYSICAL) {
            return PhysicalEvent.builder().title(dto.getTitle()).description(dto.getDescription())
                    .date(dto.getDate()).eventType(dto.getEventType()).ticketPrice(dto.getTicketPrice()).build();
        } else {
            return OnlineEvent.builder().title(dto.getTitle()).description(dto.getDescription())
                    .date(dto.getDate()).eventType(dto.getEventType()).ticketPrice(dto.getTicketPrice()).build();
        }
    }
}
