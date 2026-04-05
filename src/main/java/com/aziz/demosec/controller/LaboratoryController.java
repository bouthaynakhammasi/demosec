package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;
import com.aziz.demosec.service.ILaboratoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/laboratories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LaboratoryController {

    private final ILaboratoryService laboratoryService;

    @GetMapping("/me")
    public ResponseEntity<LaboratoryResponse> getMyLaboratory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(laboratoryService.getLaboratoryForCurrentUser(principal.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<LaboratoryResponse> updateProfile(Principal principal, @Valid @RequestBody LaboratoryStaffProfileUpdateRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(laboratoryService.updateProfile(principal.getName(), request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
