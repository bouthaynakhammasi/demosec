package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.TreatmentStatus;
import com.aziz.demosec.Entities.TreatmentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentResponse {

    private Long id;
    private Long consultationId;

    private TreatmentType treatmentType;
    private String description;
    private String dosage;

    private LocalDate startDate;
    private LocalDate endDate;

    private TreatmentStatus status;
}