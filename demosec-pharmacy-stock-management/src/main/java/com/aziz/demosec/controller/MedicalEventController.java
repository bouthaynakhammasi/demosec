package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.service.IMedicalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class MedicalEventController {
    private final IMedicalEventService medicalEventService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalEventResponse> create(
            @RequestPart("event") MedicalEventCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(medicalEventService.create(request, image));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalEventResponse> update(
            @PathVariable Long id,
            @RequestPart("event") MedicalEventUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(medicalEventService.update(id, request, image));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<MedicalEventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalEventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalEventResponse>> getAll() {
        return ResponseEntity.ok(medicalEventService.getAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MedicalEventResponse>> getUpcoming() {
        return ResponseEntity.ok(medicalEventService.getUpcoming());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participate")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> participate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        medicalEventService.participateInEvent(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/cancel-participation")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> cancelParticipation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        medicalEventService.cancelParticipation(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/is-participating")
    public ResponseEntity<?> isParticipating(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean result = medicalEventService.isParticipating(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("participating", result));
    }

    @PutMapping("/participation/{participationId}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> acceptParticipation(
            @PathVariable Long participationId) {
        medicalEventService.acceptParticipation(participationId);
        return ResponseEntity.ok(Map.of("message", "Participation accepted"));
    }

    @PutMapping("/participation/{participationId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectParticipation(
            @PathVariable Long participationId) {
        medicalEventService.rejectParticipation(participationId);
        return ResponseEntity.ok(Map.of("message", "Participation rejected"));
    }
}