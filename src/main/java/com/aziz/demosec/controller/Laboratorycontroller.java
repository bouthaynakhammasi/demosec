package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;
import com.aziz.demosec.service.ILaboratoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/laboratories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LaboratoryController {

    private final ILaboratoryService laboratoryService;

    // --- Profile Endpoints ---
    @GetMapping("/me")
    public ResponseEntity<LaboratoryResponse> getMyLaboratory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(laboratoryService.getLaboratoryForCurrentUser(principal.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<LaboratoryResponse> updateProfile(Principal principal, @Valid @RequestBody LaboratoryStaffProfileUpdateRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(laboratoryService.updateProfile(principal.getName(), request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // --- CRUD Endpoints ---
    @PostMapping
    public ResponseEntity<LaboratoryResponse> create(@Valid @RequestBody LaboratoryRequest request) {
        return new ResponseEntity<>(laboratoryService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(laboratoryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LaboratoryResponse>> getAll() {
        return ResponseEntity.ok(laboratoryService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<LaboratoryResponse>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(laboratoryService.searchByName(name));
    }

    @GetMapping("/active")
    public ResponseEntity<List<LaboratoryResponse>> getActive() {
        return ResponseEntity.ok(laboratoryService.getActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboratoryResponse> update(@PathVariable Long id, @Valid @RequestBody LaboratoryRequest request) {
        return ResponseEntity.ok(laboratoryService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<LaboratoryResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(laboratoryService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        laboratoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
