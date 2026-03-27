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
public class ProgressTrackingResponse {
   
    private Long id;

    private Long patientId;
    private Long goalId;

    private LocalDate date;

    private BigDecimal value;

    private String notes;
}
