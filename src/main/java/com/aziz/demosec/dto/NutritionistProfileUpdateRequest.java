package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record NutritionistProfileUpdateRequest(
    @NotBlank(message = "Full name is required")
    String fullName,

    String phone,
    LocalDate birthDate,
    String photo,
    String specialties,

    @Positive(message = "Consultation fee must be positive")
    BigDecimal consultationFee,

    String consultationMode
) {}
