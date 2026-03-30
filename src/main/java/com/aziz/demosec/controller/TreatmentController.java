package com.aziz.demosec.controller;

import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;
import com.aziz.demosec.service.ITreatmentService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RestController
@AllArgsConstructor
@RequestMapping("/treatment")
@Transactional
public class TreatmentController {

    private ITreatmentService treatmentService;

    @PostMapping("/add")
    public TreatmentResponse add(@Valid @RequestBody TreatmentRequest request) {
        return treatmentService.addTreatment(request);
    }

    @GetMapping("/get/{id}")
    public TreatmentResponse getByIdWithGet(@PathVariable Long id) {
        return treatmentService.selectTreatmentByIdWithGet(id);
    }

    @GetMapping("/all")
    public List<TreatmentResponse> getAll() {
        return treatmentService.selectAllTreatments();
    }

    @PutMapping("/update/{id}")
    public TreatmentResponse update(@PathVariable Long id, @Valid @RequestBody TreatmentRequest request) {
        return treatmentService.updateTreatment(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        treatmentService.deleteTreatmentById(id);
    }
}