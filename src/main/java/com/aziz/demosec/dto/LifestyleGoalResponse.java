package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleGoalResponse {
     private Long id;

    private Long patientId;

    private String category;

    private BigDecimal targetValue;
    private BigDecimal baselineValue;

    private LocalDate targetDate;

    private String status;

    private List<LifestylePlanResponse> plans;
}
