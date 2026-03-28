package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleGoalRequest {
  

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    private BigDecimal targetValue;

    @NotNull(message = "Baseline value is required")
    @Positive(message = "Baseline value must be positive")
    private BigDecimal baselineValue;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;
}
