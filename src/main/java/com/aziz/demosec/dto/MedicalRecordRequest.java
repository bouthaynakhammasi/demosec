package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MedicalRecordRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;
}