package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;
import com.aziz.demosec.service.ILifestyleGoalService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lifestyle-goals")
@AllArgsConstructor
public class LifestyleGoalController {

    private final ILifestyleGoalService lifestyleGoalService;

    @PostMapping
    public ResponseEntity<LifestyleGoalResponse> addGoal(@Valid @RequestBody LifestyleGoalRequest request) {
        return ResponseEntity.ok(lifestyleGoalService.addGoal(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LifestyleGoalResponse> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(lifestyleGoalService.getGoalById(id));
    }

    @GetMapping
    public ResponseEntity<List<LifestyleGoalResponse>> getAllGoals() {
        return ResponseEntity.ok(lifestyleGoalService.getAllGoals());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LifestyleGoalResponse>> getGoalsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(lifestyleGoalService.getGoalsByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LifestyleGoalResponse> updateGoal(@PathVariable Long id, @Valid @RequestBody LifestyleGoalRequest request) {
        return ResponseEntity.ok(lifestyleGoalService.updateGoal(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        lifestyleGoalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
