package com.aziz.demosec.service;

import com.aziz.demosec.Entities.EventSeat;
import com.aziz.demosec.dto.MedicalEventDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MedicalEventService {
    List<MedicalEventDTO> getAllEvents();

    MedicalEventDTO getEventById(Long id);

    MedicalEventDTO createEvent(MedicalEventDTO eventDTO, MultipartFile image);

    MedicalEventDTO updateEvent(Long id, MedicalEventDTO eventDTO, MultipartFile image);

    void deleteEvent(Long id);

    // Seating
    List<EventSeat> getEventSeats(Long eventId);

    void generateLayout(Long eventId, String venueType);

    void saveSeatsBatch(Long eventId, List<EventSeat> seats);

    void reserveSeat(Long seatId);

    void releaseSeat(Long seatId);

    // Analytics
    Map<String, Object> getEventAnalytics(Long eventId);

    // Participation
    void participateInEvent(Long eventId, Long userId);

    void cancelParticipation(Long eventId, Long userId);

    Map<String, Object> isParticipating(Long eventId, Long userId);

    void acceptParticipation(Long participationId);

    void rejectParticipation(Long participationId);

    byte[] generateTicketPdf(Long participationId);
}
