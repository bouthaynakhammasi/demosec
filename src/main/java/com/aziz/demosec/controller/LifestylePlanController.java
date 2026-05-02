package com.aziz.demosec.controller;

import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;
import com.aziz.demosec.service.ILifestylePlanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lifestyle-plans")
@AllArgsConstructor
public class LifestylePlanController {

    private final ILifestylePlanService lifestylePlanService;

    @PostMapping
    public ResponseEntity<LifestylePlanResponse> addPlan(@Valid @RequestBody LifestylePlanRequest request) {
        return ResponseEntity.ok(lifestylePlanService.addPlan(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LifestylePlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(lifestylePlanService.getPlanById(id));
    }

    @GetMapping
    public ResponseEntity<List<LifestylePlanResponse>> getAllPlans() {
        return ResponseEntity.ok(lifestylePlanService.getAllPlans());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LifestylePlanResponse>> getPlansByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(lifestylePlanService.getPlansByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LifestylePlanResponse> updatePlan(@PathVariable Long id, @Valid @RequestBody LifestylePlanRequest request) {
        return ResponseEntity.ok(lifestylePlanService.updatePlan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        lifestylePlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(lifestylePlanService.countAll());
    }
}
