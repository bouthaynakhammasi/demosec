package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.dto.ClinicProfileResponse;
import com.aziz.demosec.dto.ClinicProfileUpdateRequest;
import com.aziz.demosec.repository.ClinicRepository;
import com.aziz.demosec.service.IClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final IClinicService clinicService;
    private final ClinicRepository clinicRepository;

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

    // ----------------- Profile Update -----------------
    @PutMapping("/profile")
    public ResponseEntity<ClinicProfileResponse> updateProfile(
            Principal principal,
            @Valid @RequestBody ClinicProfileUpdateRequest request) {

        if (principal == null) return ResponseEntity.status(401).build();

        return clinicRepository.findByEmail(principal.getName())
                .map(clinic -> {
                    if (request.fullName() != null) clinic.setFullName(request.fullName());
                    if (request.phone() != null) clinic.setPhone(request.phone());
                    if (request.birthDate() != null) clinic.setBirthDate(request.birthDate());
                    if (request.photo() != null) clinic.setPhoto(request.photo());
                    if (request.clinicName() != null) clinic.setName(request.clinicName());
                    if (request.address() != null) clinic.setAddress(request.address());
                    if (request.latitude() != null) clinic.setLatitude(request.latitude());
                    if (request.longitude() != null) clinic.setLongitude(request.longitude());
                    if (request.hasEmergency() != null) clinic.setHasEmergency(request.hasEmergency());
                    if (request.hasAmbulance() != null) clinic.setHasAmbulance(request.hasAmbulance());
                    if (request.emergencyPhone() != null) clinic.setEmergencyPhone(request.emergencyPhone());
                    if (request.ambulancePhone() != null) clinic.setAmbulancePhone(request.ambulancePhone());

                    Clinic saved = clinicRepository.save(clinic);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ClinicProfileResponse mapToResponse(Clinic clinic) {
        return new ClinicProfileResponse(
                clinic.getId(),
                clinic.getFullName(),
                clinic.getEmail(),
                clinic.getPhone(),
                clinic.getBirthDate(),
                clinic.getPhoto(),
                clinic.getName(),
                clinic.getAddress(),
                clinic.getLatitude(),
                clinic.getLongitude(),
                clinic.isHasEmergency(),
                clinic.isHasAmbulance(),
                clinic.getEmergencyPhone(),
                clinic.getAmbulancePhone()
        );
    }
}