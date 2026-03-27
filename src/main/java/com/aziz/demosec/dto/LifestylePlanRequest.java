package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestylePlanRequest {
     private Long goalId;
    private Long nutritionistId;

    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
}
