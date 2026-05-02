package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.AnomalyType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyHealthReportDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long lifestylePlanId;
    private LocalDate reportDate;

    private Integer actualCalories;
    private Integer expectedCalories;
    private Integer calorieDifference;

    private Double currentWeight;
    private Double goalWeight;
    private Double weightDifference;

    private boolean missedLog;
    private boolean anomalyDetected;
    private List<AnomalyType> anomalies;
    private String patientPhoto;
}