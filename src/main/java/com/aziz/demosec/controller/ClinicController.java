package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClinicController {

    private final ClinicRepository clinicRepository;

    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicRepository.findAll();
        System.out.println("[DEBUG] Cliniques trouvées: " + clinics.size());
        return ResponseEntity.ok(clinics);
    }
}
