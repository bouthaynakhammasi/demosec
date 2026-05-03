package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MealPlanDTO;
import com.aziz.demosec.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping
    public ResponseEntity<MealPlanDTO> create(@RequestBody MealPlanDTO dto) {
        return ResponseEntity.ok(mealPlanService.create(dto));
    }

    @PostMapping("/bulk/{lifestylePlanId}")
    public ResponseEntity<List<MealPlanDTO>> saveAll(
            @PathVariable Long lifestylePlanId, @RequestBody List<MealPlanDTO> dtos) {
        return ResponseEntity.ok(mealPlanService.saveAll(lifestylePlanId, dtos));
    }

    @GetMapping("/lifestyle-plan/{lifestylePlanId}")
    public ResponseEntity<List<MealPlanDTO>> getByLifestylePlan(@PathVariable Long lifestylePlanId) {
        return ResponseEntity.ok(mealPlanService.getByLifestylePlan(lifestylePlanId));
    }

    @GetMapping("/lifestyle-plan/{lifestylePlanId}/day/{dayOfWeek}")
    public ResponseEntity<List<MealPlanDTO>> getByLifestylePlanAndDay(
            @PathVariable Long lifestylePlanId, @PathVariable String dayOfWeek) {
        return ResponseEntity.ok(mealPlanService.getByLifestylePlanAndDay(lifestylePlanId, dayOfWeek));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MealPlanDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(mealPlanService.getByPatient(patientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mealPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
