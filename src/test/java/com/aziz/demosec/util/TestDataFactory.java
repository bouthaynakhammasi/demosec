package com.aziz.demosec.util;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static User createUser(Long id, String email, Role role) {
        User user = new User();
        user.setId(id);
        user.setFullName("Test User " + id);
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    public static Patient createPatient(Long id, String email) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFullName("Patient " + id);
        patient.setEmail(email);
        patient.setPassword("password123");
        patient.setRole(Role.PATIENT);
        patient.setEnabled(true);
        return patient;
    }

    public static Doctor createDoctor(Long id, String email) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setFullName("Doctor " + id);
        doctor.setEmail(email);
        doctor.setPassword("password123");
        doctor.setRole(Role.DOCTOR);
        doctor.setEnabled(true);
        doctor.setSpecialty("General");
        return doctor;
    }

    public static Nutritionist createNutritionist(Long id, String email) {
        Nutritionist nutritionist = new Nutritionist();
        nutritionist.setId(id);
        nutritionist.setFullName("Nutritionist " + id);
        nutritionist.setEmail(email);
        nutritionist.setPassword("password123");
        nutritionist.setRole(Role.NUTRITIONIST);
        nutritionist.setEnabled(true);
        return nutritionist;
    }

    public static LifestyleGoal createLifestyleGoal(Long id, User patient) {
        LifestyleGoal goal = new LifestyleGoal();
        goal.setId(id);
        goal.setPatient(patient);
        goal.setBaselineValue(new BigDecimal("100.0"));
        goal.setTargetValue(new BigDecimal("90.0"));
        goal.setTargetDate(LocalDate.now().plusMonths(1));
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setCategory(GoalCategory.WEIGHT_LOSS);
        return goal;
    }

    public static LifestylePlan createLifestylePlan(Long id, LifestyleGoal goal, User nutritionist) {
        LifestylePlan plan = new LifestylePlan();
        plan.setId(id);
        plan.setGoal(goal);
        plan.setNutritionist(nutritionist);
        plan.setTitle("Plan " + id);
        plan.setDescription("Description " + id);
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusMonths(1));
        plan.setStatus(PlanStatus.ACTIVE);
        return plan;
    }

    public static ProgressTracking createProgressTracking(Long id, LifestyleGoal goal, User patient) {
        ProgressTracking tracking = new ProgressTracking();
        tracking.setId(id);
        tracking.setGoal(goal);
        tracking.setPatient(patient);
        tracking.setDate(LocalDate.now());
        tracking.setValue(new BigDecimal("95.0"));
        tracking.setNotes("Notes " + id);
        return tracking;
    }

    public static MedicalRecord createMedicalRecord(Long id, User patient) {
        MedicalRecord record = new MedicalRecord();
        record.setId(id);
        record.setPatient(patient);
        return record;
    }

    public static Consultation createConsultation(Long id, MedicalRecord medicalRecord, User doctor) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setMedicalRecord(medicalRecord);
        consultation.setDoctor(doctor);
        consultation.setDate(LocalDateTime.now());
        consultation.setNotes("General checkup");
        return consultation;
    }

    public static Diagnosis createDiagnosis(Long id, Consultation consultation) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(id);
        diagnosis.setConsultation(consultation);
        diagnosis.setDescription("Common Cold");
        diagnosis.setType(DiagnosisType.PRIMARY);
        return diagnosis;
    }

    public static Treatment createTreatment(Long id, Consultation consultation) {
        Treatment treatment = new Treatment();
        treatment.setId(id);
        treatment.setConsultation(consultation);
        treatment.setDescription("Rest and fluids");
        treatment.setStartDate(LocalDate.now());
        treatment.setEndDate(LocalDate.now().plusDays(7));
        treatment.setStatus(TreatmentStatus.ONGOING);
        treatment.setTreatmentType(TreatmentType.MEDICATION);
        return treatment;
    }

    public static Prescription createPrescription(Long id, Consultation consultation) {
        Prescription prescription = new Prescription();
        prescription.setId(id);
        prescription.setConsultation(consultation);
        prescription.setDate(LocalDate.now());
        return prescription;
    }
}
