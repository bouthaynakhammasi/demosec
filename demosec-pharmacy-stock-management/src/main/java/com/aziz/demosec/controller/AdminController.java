package com.aziz.demosec.controller;

import com.aziz.demosec.entities.Pharmacist;
import com.aziz.demosec.entities.PharmacistStatus;
import com.aziz.demosec.repository.PharmacistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PharmacistRepository pharmacistRepository;

    @GetMapping("/pharmacists/pending")
    public ResponseEntity<List<Pharmacist>> getPendingPharmacistSetups() {
        return ResponseEntity.ok(pharmacistRepository.findByStatus(PharmacistStatus.PENDING));
    }

    @PatchMapping("/pharmacists/{id}/approve")
    public ResponseEntity<?> approvePharmacistSetup(@PathVariable("id") Long id) {
        Pharmacist pharmacist = pharmacistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pharmacist not found"));
        pharmacist.setStatus(PharmacistStatus.APPROVED);
        pharmacistRepository.save(pharmacist);
        return ResponseEntity.ok(Map.of("success", true, "message", "Pharmacist approved"));
    }

    @PatchMapping("/pharmacists/{id}/reject")
    public ResponseEntity<?> rejectPharmacistSetup(@PathVariable("id") Long id) {
        Pharmacist pharmacist = pharmacistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pharmacist not found"));
        pharmacist.setStatus(PharmacistStatus.REJECTED);
        pharmacistRepository.save(pharmacist);
        return ResponseEntity.ok(Map.of("success", true, "message", "Pharmacist rejected"));
    }
}
