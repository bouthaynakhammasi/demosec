package com.aziz.demosec.controller;

import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;
import com.aziz.demosec.service.IPrescriptionService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RestController
@AllArgsConstructor
@RequestMapping("/prescription")
@Transactional
public class PrescriptionController {

    private IPrescriptionService prescriptionService;

    @PostMapping("/add")
    public PrescriptionResponse add(@Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.addPrescription(request);
    }

    @GetMapping("/get/{id}")
    public PrescriptionResponse getByIdWithGet(@PathVariable Long id) {
        return prescriptionService.selectPrescriptionByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public PrescriptionResponse getByIdWithOrElse(@PathVariable Long id) {
        return prescriptionService.selectPrescriptionByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<PrescriptionResponse> getAll() {
        return prescriptionService.selectAllPrescriptions();
    }

    @PutMapping("/update/{id}")
    public PrescriptionResponse update(@PathVariable Long id, @Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.updatePrescription(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        prescriptionService.deletePrescriptionById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        prescriptionService.deleteAllPrescriptions();
    }

    @GetMapping("/count")
    public long count() {
        return prescriptionService.countingPrescriptions();
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return prescriptionService.verifPrescriptionById(id);
    }
}
