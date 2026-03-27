package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;
import com.aziz.demosec.service.IProgressTrackingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress-tracking")
@AllArgsConstructor
public class ProgressTrackingController {

    private final IProgressTrackingService progressTrackingService;

    @PostMapping
    public ResponseEntity<ProgressTrackingResponse> addTracking(@RequestBody ProgressTrackingRequest request) {
        return ResponseEntity.ok(progressTrackingService.addTracking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressTrackingResponse> getTrackingById(@PathVariable Long id) {
        return ResponseEntity.ok(progressTrackingService.getTrackingById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProgressTrackingResponse>> getAllTrackings() {
        return ResponseEntity.ok(progressTrackingService.getAllTrackings());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ProgressTrackingResponse>> getTrackingsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(progressTrackingService.getTrackingsByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressTrackingResponse> updateTracking(@PathVariable Long id, @RequestBody ProgressTrackingRequest request) {
        return ResponseEntity.ok(progressTrackingService.updateTracking(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTracking(@PathVariable Long id) {
        progressTrackingService.deleteTracking(id);
        return ResponseEntity.noContent().build();
    }
}
