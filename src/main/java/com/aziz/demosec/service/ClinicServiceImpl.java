package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements IClinicService {

    private final ClinicRepository clinicRepository;

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public Optional<Clinic> getClinicById(Long id) {
        return clinicRepository.findById(id);
    }

    public Clinic createClinic(Clinic clinic) {
        // Initialiser les champs obligatoires si non fournis
        if (!clinic.isHasEmergency()) clinic.setHasEmergency(true);
        if (!clinic.isHasAmbulance()) clinic.setHasAmbulance(true);

        return clinicRepository.save(clinic);
    }

    public Clinic updateClinic(Long id, Clinic clinicDetails) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        clinic.setName(clinicDetails.getName());
        clinic.setAddress(clinicDetails.getAddress());
        clinic.setLatitude(clinicDetails.getLatitude());
        clinic.setLongitude(clinicDetails.getLongitude());
        clinic.setPhone(clinicDetails.getPhone());
        clinic.setHasEmergency(clinicDetails.isHasEmergency());
        clinic.setHasAmbulance(clinicDetails.isHasAmbulance());
        return clinicRepository.save(clinic);
    }

    public void deleteClinic(Long id) {
        clinicRepository.deleteById(id);
    }
}