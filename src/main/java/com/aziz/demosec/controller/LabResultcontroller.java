package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.service.LabResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}