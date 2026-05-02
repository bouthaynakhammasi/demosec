package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientSummaryDTO {
    private Long patientId;
    private String patientName;
    private LocalDateTime lastConsultationDate;
    private String lastDiagnosis;
    private String medication;
    private String lifestylePlanGoal;
}
