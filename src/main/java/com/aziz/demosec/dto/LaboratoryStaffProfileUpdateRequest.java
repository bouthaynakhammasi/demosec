package com.aziz.demosec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record LaboratoryStaffProfileUpdateRequest(
    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    String phone,
    LocalDate birthDate,
    String photo,

    // Laboratory specific info
    String laboratoryName,
    String laboratoryAddress,
    String laboratoryPhone
) {}
