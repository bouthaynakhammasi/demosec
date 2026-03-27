package com.aziz.demosec.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NutritionistProfileUpdateRequest(
    String fullName,
    String phone,
    LocalDate birthDate,
    String photo,
    String specialties,
    BigDecimal consultationFee,
    String consultationMode
) {}
