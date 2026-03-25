package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.ConsultationMode;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        Role role,
        String phone,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        // Champs Patient uniquement (nullable si autre rôle)

        Gender gender,
        BloodType bloodType,
        String emergencyContactName,
        String emergencyContactPhone,
        String glucoseRate,
        String allergies,
        String diseases,

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

        // Champs ServiceProvider
        String certificationDocument
) {}
