package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.service.IClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private  IClinicService clinicService;

    @GetMapping
    public List<Clinic> getAllClinics() {
        return clinicService.getAllClinics();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable Long id) {
        return clinicService.getClinicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Clinic createClinic(@RequestBody Clinic clinic) {
        return clinicService.createClinic(clinic);
    }

    @PutMapping("/{id}")
    public Clinic updateClinic(@PathVariable Long id, @RequestBody Clinic clinicDetails) {
        return clinicService.updateClinic(id, clinicDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok().build();
    }
}