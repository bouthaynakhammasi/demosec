package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotNull(message = "Full name is required")
        String fullName,
        @Email(message = "Invalid email format")
        @NotNull(message = "Email is required")
        String email,
        @NotNull(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters ")
        String password,
        @NotNull(message = "Role is required")
        Role role,
        @Size(min = 8, message = "Phone number must be at least 8 characters ")
        String phone,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        // Champs Patient uniquement (nullable si autre rôle)

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
        // Champs Pharmacist uniquement (nullable si autre rôle)
        String pharmacyName,
        String pharmacyAddress,
        String pharmacyPhone,

        // Champs Home Care Provider uniquement
        java.util.List<Long> specialtyIds
) {}

