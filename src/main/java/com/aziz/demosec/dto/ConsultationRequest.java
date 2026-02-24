package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultationRequest {

    private Long medicalRecordId;
    private Long doctorId;

    private LocalDateTime date;
    private String observations;
    private String notes;
}