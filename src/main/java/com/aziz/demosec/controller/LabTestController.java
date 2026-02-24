package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import com.aziz.demosec.service.LabTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-tests")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping
    public ResponseEntity<LabTestResponse> create(@Valid @RequestBody LabTestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(labTestService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(labTestService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LabTestResponse>> getAll() {
        return ResponseEntity.ok(labTestService.getAll());
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<LabTestResponse>> getByLaboratory(@PathVariable Long laboratoryId) {
        return ResponseEntity.ok(labTestService.getByLaboratory(laboratoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabTestResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody LabTestRequest request) {
        return ResponseEntity.ok(labTestService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labTestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}