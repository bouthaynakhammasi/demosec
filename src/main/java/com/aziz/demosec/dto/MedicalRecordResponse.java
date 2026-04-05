package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private Long id;
    private Long patientId;
}