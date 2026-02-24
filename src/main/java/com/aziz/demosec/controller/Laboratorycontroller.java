package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.service.LaboratoryServiceImpl;
import com.aziz.demosec.service.LaboratoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratories")
@RequiredArgsConstructor
public class Laboratorycontroller {

    private final LaboratoryServiceImpl laboratoryService;

    @PostMapping
    public ResponseEntity<LaboratoryResponse> create(@Valid @RequestBody LaboratoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laboratoryService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(laboratoryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LaboratoryResponse>> getAll() {
        return ResponseEntity.ok(laboratoryService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody LaboratoryRequest request) {
        return ResponseEntity.ok(laboratoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        laboratoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}