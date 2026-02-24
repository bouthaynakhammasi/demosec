package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.MedicalHistoryStatus;
import com.aziz.demosec.Entities.MedicalHistoryType;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MedicalHistoryRequest {

    private Long medicalRecordId;

    private MedicalHistoryType type;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private MedicalHistoryStatus status;
}