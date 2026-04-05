package com.aziz.demosec.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NutritionistProfileResponse(
    Long id,
    String fullName,
    String email,
    String phone,
    LocalDate birthDate,
    String photo,
    String licenseNumber,
    String specialties,
    BigDecimal consultationFee,
    String consultationMode
) {}
