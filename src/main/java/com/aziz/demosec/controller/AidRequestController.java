package com.aziz.demosec.controller;


import com.aziz.demosec.Entities.AidRequestStatus;
import com.aziz.demosec.dto.donation.*;
import com.aziz.demosec.service.IDonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/aid-requests")
@RequiredArgsConstructor
public class AidRequestController {

    private final IDonationService donationService;
    
    @PostMapping
    public ResponseEntity<AidRequestResponseDTO> create(@Valid @RequestBody AidRequestDTO dto) {
        return ResponseEntity.ok(donationService.createAidRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<AidRequestResponseDTO>> getAll() {
        return ResponseEntity.ok(donationService.getAllAidRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AidRequestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getAidRequestById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AidRequestResponseDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(donationService.getAidRequestsByPatient(patientId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AidRequestResponseDTO> updateStatus(
            @PathVariable Long id, @RequestParam AidRequestStatus status) {
        return ResponseEntity.ok(donationService.updateAidRequestStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AidRequestResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody AidRequestDTO dto) {
        return ResponseEntity.ok(donationService.updateAidRequest(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.deleteAidRequest(id);
        return ResponseEntity.noContent().build();
    }
}