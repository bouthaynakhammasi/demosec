package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

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
        String chronicDiseases,
        String drugAllergies,
        String hereditaryDiseases,

        // ✅ Laboratory Staff
        String labName,
        String labAddress,
        String labPhone
) {}