
package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.EmergencyAlertStatus;
import com.aziz.demosec.dto.emergency.*;
import com.aziz.demosec.service.IEmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emergency-alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final IEmergencyService emergencyService;

    @PostMapping
    public ResponseEntity<EmergencyAlertResponseDTO> create(@RequestBody EmergencyAlertRequestDTO dto) {
        return ResponseEntity.ok(emergencyService.createAlert(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmergencyAlertResponseDTO>> getAll() {
        return ResponseEntity.ok(emergencyService.getAllAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyAlertResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(emergencyService.getAlertById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmergencyAlertResponseDTO>> getByStatus(
            @PathVariable EmergencyAlertStatus status) {
        return ResponseEntity.ok(emergencyService.getAlertsByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EmergencyAlertResponseDTO> updateStatus(
            @PathVariable Long id, @RequestParam EmergencyAlertStatus status) {
        return ResponseEntity.ok(emergencyService.updateAlertStatus(id, status));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<EmergencyAlertResponseDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(emergencyService.cancelAlertByPatient(id));
    }
}