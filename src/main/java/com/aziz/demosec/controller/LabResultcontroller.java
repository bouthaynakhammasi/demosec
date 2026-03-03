package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.service.LabResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lab-results")
@RequiredArgsConstructor
public class LabResultcontroller {

    private final LabResultService labResultService;

    @PostMapping
    public ResponseEntity<LabResultResponse> create(@Valid @RequestBody LabResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(labResultService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LabResultResponse>> getAll() {
        return ResponseEntity.ok(labResultService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResultResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(labResultService.getById(id));
    }

    @GetMapping("/by-request/{labRequestId}")
    public ResponseEntity<LabResultResponse> getByLabRequestId(@PathVariable Long labRequestId) {
        return ResponseEntity.ok(labResultService.getByLabRequestId(labRequestId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabResultResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LabResultRequest request) {
        return ResponseEntity.ok(labResultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labResultService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints innovants
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LabResultResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(labResultService.getByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<LabResultResponse>> getByPriority(@PathVariable Integer priority) {
        return ResponseEntity.ok(labResultService.getByPriority(priority));
    }

    @GetMapping("/urgent")
    public ResponseEntity<List<LabResultResponse>> getUrgentResults() {
        return ResponseEntity.ok(labResultService.getUrgentResults());
    }

    @GetMapping("/abnormal")
    public ResponseEntity<List<LabResultResponse>> getAbnormalResults() {
        return ResponseEntity.ok(labResultService.getAbnormalResults());
    }

    @GetMapping("/technician/{technicianName}")
    public ResponseEntity<List<LabResultResponse>> getByTechnicianName(@PathVariable String technicianName) {
        return ResponseEntity.ok(labResultService.getByTechnicianName(technicianName));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LabResultResponse>> getByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(labResultService.getByDateRange(startDate, endDate));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LabResultResponse>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(labResultService.searchByKeyword(keyword));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<LabResultResponse> verifyResult(
            @PathVariable Long id,
            @RequestParam String verifiedBy) {
        return ResponseEntity.ok(labResultService.verifyResult(id, verifiedBy));
    }

    @PatchMapping("/{id}/urgent")
    public ResponseEntity<LabResultResponse> markAsUrgent(@PathVariable Long id) {
        return ResponseEntity.ok(labResultService.markAsUrgent(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LabResultResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(labResultService.updateStatus(id, status));
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalResults() {
        return ResponseEntity.ok(labResultService.getTotalResults());
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> getResultsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(labResultService.getResultsByStatus(status));
    }

    @GetMapping("/technicians")
    public ResponseEntity<List<String>> getAvailableTechnicians() {
        return ResponseEntity.ok(labResultService.getAvailableTechnicians());
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<List<LabResultResponse>> getRecentResults(@PathVariable int days) {
        return ResponseEntity.ok(labResultService.getRecentResults(days));
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<LabResultResponse> duplicateResult(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(labResultService.duplicateResult(id));
    }
}