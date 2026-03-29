package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.ConsultationMode;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public record RegisterRequest(
        String fullName,
        String email,
        String password,
        Role role,
        String phone,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        // Patient
        Gender gender,
        BloodType bloodType,
        String emergencyContactName,
        String emergencyContactPhone,

        String glucoseRate,
        String allergies,
        String diseases,
        Double height,
        Double weight,

        // Champs Doctor
        String specialty,
        String licenseNumber,
        BigDecimal consultationFee,
        ConsultationMode consultationMode,

        // Champs Clinic
        String clinicName,
        String clinicAddress,
        String clinicPhone,
        String emergencyPhone,
        String ambulancePhone,

        // Champs LaboratoryStaff
        String labName,
        String labAddress,
        String labPhone,

        // Champs Pharmacist
        String pharmacyName,
        String pharmacyAddress,
        String pharmacyPhone,
        String pharmacyEmail,

        // Champs ServiceProvider
        String certificationDocument,
        List<String> homeCareServices
) {}
