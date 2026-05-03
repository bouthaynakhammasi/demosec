package com.aziz.demosec.controller;

import com.aziz.demosec.dto.FoodDiaryDTO;
import com.aziz.demosec.service.FoodDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/food-diaries")
@RequiredArgsConstructor
public class FoodDiaryController {

    private final FoodDiaryService foodDiaryService;

    @PostMapping
    public ResponseEntity<FoodDiaryDTO> create(@RequestBody FoodDiaryDTO dto) {
        return ResponseEntity.ok(foodDiaryService.create(dto));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FoodDiaryDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(foodDiaryService.getByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<FoodDiaryDTO>> getByPatientAndDate(
            @PathVariable Long patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(foodDiaryService.getByPatientAndDate(patientId, date));
    }

    @GetMapping("/patient/{patientId}/daily-calories/{date}")
    public ResponseEntity<Integer> getDailyCalories(
            @PathVariable Long patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(foodDiaryService.getDailyCalories(patientId, date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodDiaryDTO> update(@PathVariable Long id, @RequestBody FoodDiaryDTO dto) {
        return ResponseEntity.ok(foodDiaryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodDiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
