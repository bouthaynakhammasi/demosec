package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.baby.*;
import com.aziz.demosec.service.BabyCareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/baby-care")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BabyCareController {
    private final BabyCareService babyService;

    @PostMapping("/profile")
    public ResponseEntity<BabyProfileResponseDTO> createProfile(@RequestParam Long parentId, @RequestBody BabyProfileRequestDTO dto) {
        return ResponseEntity.ok(babyService.createProfile(parentId, dto));
    }

    @GetMapping("/profile/{patientId}")
    public ResponseEntity<BabyProfileResponseDTO> getProfileByPatientId(@PathVariable Long patientId) {
        BabyProfileResponseDTO profile = babyService.getProfileByPatientId(patientId);
        if (profile == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/dashboard/{babyId}")
    public ResponseEntity<BabyDashboardDTO> getDashboard(@PathVariable Long babyId) {
        return ResponseEntity.ok(babyService.getDashboard(babyId));
    }

    @GetMapping("/vaccines/{babyId}")
    public ResponseEntity<VaccinationOverviewDTO> getVaccines(@PathVariable Long babyId) {
        return ResponseEntity.ok(babyService.getVaccinationOverview(babyId));
    }

    @GetMapping("/journal/{babyId}")
    public ResponseEntity<List<JournalEntryResponseDTO>> getJournal(@PathVariable Long babyId) {
        return ResponseEntity.ok(babyService.getJournal(babyId));
    }

    @PostMapping("/journal/{babyId}")
    public ResponseEntity<JournalEntryResponseDTO> addJournalEntry(
            @PathVariable Long babyId,
            @RequestBody JournalEntryRequestDTO dto) {
        return ResponseEntity.ok(babyService.addJournalEntry(babyId, dto.getType(), dto.getValue(), dto.getNotes()));
    }

    @DeleteMapping("/journal/{id}")
    public ResponseEntity<Void> deleteJournalEntry(@PathVariable Long id) {
        babyService.deleteJournalEntry(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/vaccines/{babyId}/administered")
    public ResponseEntity<VaccineSummaryDTO> addVaccineRecord(
            @PathVariable Long babyId,
            @RequestParam String name,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(babyService.addVaccineRecord(babyId, name, date));
    }

    @DeleteMapping("/vaccines/{babyId}/reset")
    public ResponseEntity<Void> resetVaccineRecord(
            @PathVariable Long babyId,
            @RequestParam String name) {
        babyService.deleteVaccineRecord(babyId, name);
        return ResponseEntity.ok().build();
    }

    // DIAPER ENDPOINTS
    @GetMapping("/diapers/{babyId}")
    public ResponseEntity<List<DiaperRecordDTO>> getDiapers(@PathVariable Long babyId) {
        return ResponseEntity.ok(babyService.getDiapers(babyId));
    }

    @PostMapping("/diapers/{babyId}")
    public ResponseEntity<DiaperRecordDTO> addDiaper(@PathVariable Long babyId, @RequestBody DiaperRecordDTO dto) {
        return ResponseEntity.ok(babyService.addDiaper(babyId, dto));
    }

    @PutMapping("/diapers/record/{id}")
    public ResponseEntity<DiaperRecordDTO> updateDiaper(@PathVariable Long id, @RequestBody DiaperRecordDTO dto) {
        return ResponseEntity.ok(babyService.updateDiaper(id, dto));
    }

    @DeleteMapping("/diapers/record/{id}")
    public ResponseEntity<Void> deleteDiaper(@PathVariable Long id) {
        babyService.deleteDiaper(id);
        return ResponseEntity.ok().build();
    }
}
