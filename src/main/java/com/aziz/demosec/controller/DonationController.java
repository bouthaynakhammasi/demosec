package com.aziz.demosec.controller;



import com.aziz.demosec.Entities.AidRequestStatus;
import com.aziz.demosec.Entities.DonationStatus;
import com.aziz.demosec.dto.donation.*;
import com.aziz.demosec.service.DonationService;
import com.aziz.demosec.service.IDonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {
    private final IDonationService donationService;

    // ─── DONATIONS ───────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<DonationResponseDTO> create(@RequestBody DonationRequestDTO dto) {
        return ResponseEntity.ok(donationService.createDonation(dto));
    }

    @GetMapping
    public ResponseEntity<List<DonationResponseDTO>> getAll() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DonationResponseDTO>> getByStatus(@PathVariable DonationStatus status) {
        return ResponseEntity.ok(donationService.getDonationsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonationResponseDTO> update(
            @PathVariable Long id, @RequestBody DonationRequestDTO dto) {
        return ResponseEntity.ok(donationService.updateDonation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }

    // ─── ASSIGNMENTS ─────────────────────────────────────────────

    @PostMapping("/assign")
    public ResponseEntity<DonationAssignmentResponseDTO> assign(
            @RequestBody DonationAssignmentDTO dto) {
        return ResponseEntity.ok(donationService.assignDonation(dto));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<DonationAssignmentResponseDTO>> getAllAssignments() {
        return ResponseEntity.ok(donationService.getAllAssignments());
    }
}
