package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.DiagnosisType;
import com.aziz.demosec.Entities.TreatmentStatus;
import com.aziz.demosec.Entities.TreatmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Flat projection returned by the JPQL patient-history query.
 * One row per (consultation × diagnosis × treatment).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientHistoryDTO {

    // --- Patient / Record ---
    private Long   medicalRecordId;
    private String patientFullName;
    private String patientEmail;

    // --- Consultation ---
    private Long          consultationId;
    private LocalDateTime consultationDate;
    private String        doctorFullName;
    private String        observations;
    private String        notes;

    // --- Diagnosis ---
    private Long          diagnosisId;
    private String        diagnosisDescription;
    private DiagnosisType diagnosisType;

    // --- Treatment ---
    private Long            treatmentId;
    private TreatmentType   treatmentType;
    private String          treatmentDescription;
    private LocalDate       treatmentStart;
    private LocalDate       treatmentEnd;
    private TreatmentStatus treatmentStatus;
}