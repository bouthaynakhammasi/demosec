package com.aziz.demosec.controller;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.service.NotificationService;
import com.aziz.demosec.service.HomeCareManagementService;
import com.aziz.demosec.Entities.ServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final HomeCareManagementService homeCareService;

    @GetMapping("/pending-pharmacists")
    public ResponseEntity<List<User>> getPendingPharmacists() {
        return ResponseEntity.ok(userRepository.findByRoleAndEnabledFalse(Role.PHARMACIST));
    }

    @PatchMapping("/approve-pharmacist/{id}")
    public ResponseEntity<Void> approvePharmacist(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEnabled(true);
        userRepository.save(user);
        
        notificationService.notifyAccountActivated(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reject-pharmacist/{id}")
    public ResponseEntity<Void> rejectPharmacist(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isEnabled()) {
            throw new RuntimeException("Cannot reject an already enabled user");
        }
        
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    // ── Home Care Provider Validations ───────────────────────────────────

    @GetMapping("/homecare/pending")
    public ResponseEntity<List<ServiceProvider>> getPendingProviders() {
        return ResponseEntity.ok(homeCareService.getPendingProviders());
    }

    @PutMapping("/homecare/approve/{id}")
    public ResponseEntity<ServiceProvider> approveProvider(@PathVariable("id") Long id) {
        return ResponseEntity.ok(homeCareService.verifyProvider(id));
    }

    @DeleteMapping("/homecare/reject/{id}")
    public ResponseEntity<Void> rejectProvider(@PathVariable("id") Long id) {
        homeCareService.rejectProvider(id);
        return ResponseEntity.noContent().build();
    }
}
