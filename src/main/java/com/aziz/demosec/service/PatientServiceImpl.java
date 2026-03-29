package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.patient.PatientRequestDTO;
import com.aziz.demosec.dto.patient.PatientResponseDTO;
import com.aziz.demosec.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements IPatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PatientResponseDTO create(PatientRequestDTO dto) {

        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        }

        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setRole(Role.PATIENT);
        patient.setPhone(dto.getPhone());
        patient.setBirthDate(dto.getBirthDate() != null ? dto.getBirthDate().toString() : null);

        patient.setGender(dto.getGender());
        patient.setBloodType(dto.getBloodType());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());

        // ✅ Nouveaux champs
        patient.setChronicDiseases(dto.getChronicDiseases());
        patient.setDrugAllergies(dto.getDrugAllergies());
        patient.setHereditaryDiseases(dto.getHereditaryDiseases());

        patient.setEnabled(true);

        return toDTO(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAll() {
        return patientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {

        Patient patient = findOrThrow(id);

        if (!patient.getEmail().equals(dto.getEmail())
                && patientRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        }

        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setPhone(dto.getPhone());
        patient.setBirthDate(dto.getBirthDate() != null ? dto.getBirthDate().toString() : null);

        patient.setGender(dto.getGender());
        patient.setBloodType(dto.getBloodType());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());

        // ✅ Nouveaux champs
        patient.setChronicDiseases(dto.getChronicDiseases());
        patient.setDrugAllergies(dto.getDrugAllergies());
        patient.setHereditaryDiseases(dto.getHereditaryDiseases());

        return toDTO(patientRepository.save(patient));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        patientRepository.deleteById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        Patient patient = findOrThrow(id);
        patient.setEnabled(!patient.isEnabled());
        patientRepository.save(patient);
    }

    private Patient findOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Patient not found with id: " + id));
    }

    private PatientResponseDTO toDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .birthDate(patient.getBirthDate() != null && !patient.getBirthDate().isEmpty() ? java.time.LocalDate.parse(patient.getBirthDate()) : null)
                .enabled(patient.isEnabled())
                .gender(patient.getGender())
                .bloodType(patient.getBloodType())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .chronicDiseases(patient.getChronicDiseases())
                .drugAllergies(patient.getDrugAllergies())
                .hereditaryDiseases(patient.getHereditaryDiseases())
                .build();
    }
}