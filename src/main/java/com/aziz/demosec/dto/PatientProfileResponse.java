package com.aziz.demosec.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientProfileResponse(
    Long id,
    String fullName,
    String email,
    String phone,
    LocalDate birthDate,
    String gender,
    String bloodType,
    String glucoseRate,
    boolean hasMedicalRecord,
    List<String> allergies,
    List<String> diseases,
    List<ConsultationResponse> consultations,
    List<TreatmentResponse> treatments,
    List<PrescriptionResponse> prescriptions,
    List<DiagnosisResponse> diagnoses,
    List<LifestyleGoalResponse> lifestyleGoals,
    List<LifestylePlanResponse> lifestylePlans,
    List<ProgressTrackingResponse> progressTrackings
) {
}
