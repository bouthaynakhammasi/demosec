package com.aziz.demosec.dto;

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
  

    private Long patientId;

    private String category;

    private BigDecimal targetValue;
    private BigDecimal baselineValue;

    private LocalDate targetDate;
}
