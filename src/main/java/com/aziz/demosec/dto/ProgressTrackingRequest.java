package com.aziz.demosec.dto;

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
public class ProgressTrackingRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Goal ID is required")
    private Long goalId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;

    private String notes;
}
