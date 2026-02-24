package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MedicalRecordRequest {
    private Long patientId;
}