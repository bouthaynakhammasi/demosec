package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalHistoryRequest;
import com.aziz.demosec.dto.MedicalHistoryResponse;
import com.aziz.demosec.service.IMedicalHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/medical-history")
public class MedicalHistoryController {

    private IMedicalHistoryService medicalHistoryService;

    @PostMapping("/add")
    public MedicalHistoryResponse add(@RequestBody MedicalHistoryRequest request) {
        return medicalHistoryService.addMedicalHistory(request);
    }

    @GetMapping("/get/{id}")
    public MedicalHistoryResponse getByIdWithGet(@PathVariable Long id) {
        return medicalHistoryService.selectMedicalHistoryByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public MedicalHistoryResponse getByIdWithOrElse(@PathVariable Long id) {
        return medicalHistoryService.selectMedicalHistoryByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<MedicalHistoryResponse> getAll() {
        return medicalHistoryService.selectAllMedicalHistories();
    }

    @PutMapping("/update/{id}")
    public MedicalHistoryResponse update(@PathVariable Long id, @RequestBody MedicalHistoryRequest request) {
        return medicalHistoryService.updateMedicalHistory(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        medicalHistoryService.deleteMedicalHistoryById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        medicalHistoryService.deleteAllMedicalHistories();
    }

    @GetMapping("/count")
    public long count() {
        return medicalHistoryService.countingMedicalHistories();
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return medicalHistoryService.verifMedicalHistoryById(id);
    }
}