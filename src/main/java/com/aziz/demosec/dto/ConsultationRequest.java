package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultationRequest {

    @NotNull(message = "Medical record ID is required")
    private Long medicalRecordId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    @NotBlank(message = "Observations are required")
    private String observations;

    private String notes;
}