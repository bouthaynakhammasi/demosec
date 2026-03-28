package com.aziz.demosec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record PatientProfileUpdateRequest(
    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    String phone,
    LocalDate birthDate,
    String gender,
    String emergencyContactName,
    String emergencyContactPhone,

    @Positive(message = "Height must be positive")
    Double height,

    @Positive(message = "Weight must be positive")
    Double weight,

    String bloodType,
    String allergies,
    String diseases,
    String photo
) {}
