package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultationResponse {

    private Long id;
    private Long medicalRecordId;
    private Long doctorId;
    private String doctorName;
    private String specialty;
    private String status;

    private LocalDateTime date;
    private String observations;
    private String notes;
    private Double height;
    private Double weight;
}