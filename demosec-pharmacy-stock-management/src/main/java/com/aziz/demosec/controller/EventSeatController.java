package com.aziz.demosec.controller;

import com.aziz.demosec.dto.SaveSeatRequest;
import com.aziz.demosec.dto.SeatResponse;
import com.aziz.demosec.dto.SeatZoneSummaryResponse;
import com.aziz.demosec.service.IEventSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events/seats")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EventSeatController {

    private final IEventSeatService seatService;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<SeatResponse>> getEventSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getEventSeats(eventId));
    }

    @GetMapping("/{eventId}/summary")
    public ResponseEntity<List<SeatZoneSummaryResponse>> getEventSeatSummary(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getEventSeatSummary(eventId));
    }

    @PostMapping("/{eventId}/batch")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> saveSeatsBatch(@PathVariable Long eventId, @RequestBody List<SaveSeatRequest> requests) {
        seatService.saveSeatsBatch(eventId, requests);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{seatId}/reserve")
    public ResponseEntity<?> reserveSeat(@PathVariable Long seatId, Principal principal) {
        seatService.reserveSeat(seatId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{seatId}/release")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> releaseSeat(@PathVariable Long seatId) {
        seatService.releaseSeat(seatId);
        return ResponseEntity.ok().build();
    }
}
