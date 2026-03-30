package com.aziz.demosec.controller;

import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;
import com.aziz.demosec.service.IDiagnosisService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RestController
@AllArgsConstructor
@RequestMapping("/diagnosis")
@Transactional
public class DiagnosisController {

    private IDiagnosisService diagnosisService;

    @PostMapping("/add")
    public DiagnosisResponse add(@Valid @RequestBody DiagnosisRequest request) {
        return diagnosisService.addDiagnosis(request);
    }

    @GetMapping("/get/{id}")
    public DiagnosisResponse getByIdWithGet(@PathVariable Long id) {
        return diagnosisService.selectDiagnosisByIdWithGet(id);
    }

    @GetMapping("/all")
    public List<DiagnosisResponse> getAll() {
        return diagnosisService.selectAllDiagnoses();
    }

    @PutMapping("/update/{id}")
    public DiagnosisResponse update(@PathVariable Long id,
                                    @Valid @RequestBody DiagnosisRequest request) {
        return diagnosisService.updateDiagnosis(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosisById(id);
    }
}