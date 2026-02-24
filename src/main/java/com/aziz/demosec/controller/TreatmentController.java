package com.aziz.demosec.controller;

import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;
import com.aziz.demosec.service.ITreatmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/treatment")
public class TreatmentController {

    private ITreatmentService treatmentService;

    @PostMapping("/add")
    public TreatmentResponse add(@RequestBody TreatmentRequest request) {
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
    public TreatmentResponse update(@PathVariable Long id, @RequestBody TreatmentRequest request) {
        return treatmentService.updateTreatment(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        treatmentService.deleteTreatmentById(id);
    }
}