package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.EventSeat;
import com.aziz.demosec.dto.MedicalEventDTO;
import com.aziz.demosec.service.MedicalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class MedicalEventController {

    private final MedicalEventService eventService;

    @GetMapping
    public ResponseEntity<List<MedicalEventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalEventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<MedicalEventDTO> getPublicEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MedicalEventDTO>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<MedicalEventDTO> createEvent(
            @RequestPart("event") MedicalEventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalEventDTO> updateEvent(
            @PathVariable Long id,
            @RequestPart("event") MedicalEventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // Seating Endpoints
    @GetMapping("/seats/{eventId}")
    public ResponseEntity<List<EventSeat>> getEventSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventSeats(eventId));
    }

    @PostMapping("/seats/{eventId}/generate")
    public ResponseEntity<Void> generateLayout(@PathVariable Long eventId, @RequestParam String venueType) {
        eventService.generateLayout(eventId, venueType);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seats/{eventId}/batch")
    public ResponseEntity<Void> saveSeatsBatch(@PathVariable Long eventId, @RequestBody List<EventSeat> seats) {
        eventService.saveSeatsBatch(eventId, seats);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seats/{seatId}/reserve")
    public ResponseEntity<Void> reserveSeat(@PathVariable Long seatId) {
        eventService.reserveSeat(seatId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seats/{seatId}/release")
    public ResponseEntity<Void> releaseSeat(@PathVariable Long seatId) {
        eventService.releaseSeat(seatId);
        return ResponseEntity.ok().build();
    }

    // Analytics Endpoint
    @GetMapping("/{id}/analytics")
    public ResponseEntity<Map<String, Object>> getEventAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventAnalytics(id));
    }

    // Participation Endpoints
    @PostMapping("/{id}/participate")
    public ResponseEntity<Void> participate(@PathVariable Long id, @RequestParam Long userId) {
        eventService.participateInEvent(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/participation/{participationId}/accept")
    public ResponseEntity<Void> acceptParticipation(@PathVariable Long participationId) {
        eventService.acceptParticipation(participationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/participation/{participationId}/reject")
    public ResponseEntity<Void> rejectParticipation(@PathVariable Long participationId) {
        eventService.rejectParticipation(participationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/participation/{participationId}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long participationId) {
        byte[] pdf = eventService.generateTicketPdf(participationId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + participationId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @DeleteMapping("/{id}/cancel-participation")
    public ResponseEntity<Void> cancelParticipation(@PathVariable Long id, @RequestParam Long userId) {
        eventService.cancelParticipation(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/is-participating")
    public ResponseEntity<Map<String, Object>> isParticipating(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(eventService.isParticipating(id, userId));
    }
}
