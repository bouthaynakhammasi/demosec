package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.DiagnosisRepository;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.repository.PatientRepository;
import com.aziz.demosec.repository.PrescriptionRepository;
import com.aziz.demosec.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordSummaryService {

    @Autowired
    private ConsultationRepository consultationRepo;
    @Autowired private DiagnosisRepository diagnosisRepo;
    @Autowired private TreatmentRepository treatmentRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private MedicalRecordRepository medicalRecordRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private CohereService cohereService;
    public String getSummary(Long recordId) {
        // Step 1: Fetch patient vitals
        MedicalRecord record = medicalRecordRepo.findById(recordId).orElse(null);
        Patient patient = null;
        if (record != null && record.getPatient() != null) {
            patient = patientRepo.findById(record.getPatient().getId()).orElse(null);
        }

        // Step 2: Fetch all linked clinical data
        List<Consultation> consultations = consultationRepo.findByMedicalRecordId(recordId);
        List<Diagnosis> diagnoses = diagnosisRepo.findByConsultationMedicalRecordId(recordId);
        List<Treatment> treatments = treatmentRepo.findByConsultationMedicalRecordId(recordId);
        List<Prescription> prescriptions = prescriptionRepo.findByConsultationMedicalRecordId(recordId);

        // Step 3: Build raw structured text
        StringBuilder raw = new StringBuilder();
        
        if (patient != null) {
            raw.append("Patient Information:\n");
            raw.append("- Name: ").append(patient.getFullName()).append("\n");
            raw.append("- Blood Type: ").append(patient.getBloodType()).append("\n");
            raw.append("- Allergies: ").append(patient.getAllergies() != null ? patient.getAllergies() : "None").append("\n");
            raw.append("- Chronic Diseases: ").append(patient.getDiseases() != null ? patient.getDiseases() : "None").append("\n");
            raw.append("- Height: ").append(patient.getHeight()).append(" cm\n");
            raw.append("- Weight: ").append(patient.getWeight()).append(" kg\n");
            raw.append("\n");
        }

        raw.append("Consultations:\n");
        consultations.forEach(c -> raw.append("- ").append(c.getNotes()).append("\n"));

        raw.append("\nDiagnoses:\n");
        diagnoses.forEach(d -> raw.append("- ").append(d.getType()).append(": ").append(d.getDescription()).append("\n"));

        raw.append("\nTreatments:\n");
        treatments.forEach(t -> raw.append("- ").append(t.getDescription()).append("\n"));

        raw.append("\nPrescriptions:\n");
        prescriptions.forEach(p -> {
            raw.append("Prescription Date: ").append(p.getDate()).append("\n");
            p.getItems().forEach(item -> raw.append("  - ")
                .append(item.getMedicationName()).append(" ")
                .append(item.getDosage()).append(" (")
                .append(item.getFrequency()).append(" for ")
                .append(item.getDuration()).append(")\n"));
        });

        return cohereService.summarize(raw.toString());
    }
}