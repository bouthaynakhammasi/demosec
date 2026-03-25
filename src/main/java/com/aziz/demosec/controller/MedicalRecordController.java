package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;
import com.aziz.demosec.service.IMedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/medical-record")
public class MedicalRecordController {

    private IMedicalRecordService medicalRecordService;

    @PostMapping("/add")
    public MedicalRecordResponse addMedicalRecord(@RequestBody MedicalRecordRequest request) {
        return medicalRecordService.addMedicalRecord(request);
    }

    @GetMapping("/get/{id}")
    public MedicalRecordResponse getByIdWithGet(@PathVariable Long id) {
        return medicalRecordService.selectMedicalRecordByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public MedicalRecordResponse getByIdWithOrElse(@PathVariable Long id) {
        return medicalRecordService.selectMedicalRecordByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<MedicalRecordResponse> getAll() {
        return medicalRecordService.selectAllMedicalRecords();
    }

    @PutMapping("/update/{id}")
    public MedicalRecordResponse update(@PathVariable Long id, @RequestBody MedicalRecordRequest request) {
        return medicalRecordService.updateMedicalRecord(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecordById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        medicalRecordService.deleteAllMedicalRecords();
    }

    @GetMapping("/count")
    public long count() {
        return medicalRecordService.countingMedicalRecords();
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return medicalRecordService.verifMedicalRecordById(id);
    }

    @GetMapping("/patient/{patientId}")
    public MedicalRecordResponse getByPatientId(@PathVariable Long patientId) {
        return medicalRecordService.selectMedicalRecordByPatientId(patientId);
    }
}