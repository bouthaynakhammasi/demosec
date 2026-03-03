package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.dto.RequestedBy;
import com.aziz.demosec.service.LabRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lab-requests")
@RequiredArgsConstructor
public class LabRequestController {

    private final LabRequestService labRequestService;

    @PostMapping
    public ResponseEntity<LabRequestResponse> create(
            @Valid @RequestBody LabRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labRequestService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(labRequestService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LabRequestResponse>> getAll() {
        return ResponseEntity.ok(labRequestService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabRequestResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LabRequestRequest request) {
        return ResponseEntity.ok(labRequestService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabRequestResponse>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(labRequestService.getByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<LabRequestResponse>> getPatientHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(labRequestService.getPatientHistory(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<LabRequestResponse>> getByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(labRequestService.getByDoctor(doctorId));
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<LabRequestResponse>> getByLaboratory(
            @PathVariable Long laboratoryId) {
        return ResponseEntity.ok(labRequestService.getByLaboratory(laboratoryId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LabRequestResponse>> getByStatus(
            @PathVariable LabRequestStatus status) {
        return ResponseEntity.ok(labRequestService.getByStatus(status));
    }

    @GetMapping("/requested-by/{requestedBy}")
    public ResponseEntity<List<LabRequestResponse>> getByRequestedBy(
            @PathVariable RequestedBy requestedBy) {
        return ResponseEntity.ok(labRequestService.getByRequestedBy(requestedBy));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LabRequestResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam LabRequestStatus status) {
        return ResponseEntity.ok(labRequestService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LabRequestResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(labRequestService.cancel(id));
    }

    @PatchMapping("/{id}/notify")
    public ResponseEntity<LabRequestResponse> markNotificationSent(
            @PathVariable Long id) {
        return ResponseEntity.ok(labRequestService.markNotificationSent(id));
    }
}