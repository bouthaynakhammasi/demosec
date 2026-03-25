package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DiagnosisRequest {

    private Long consultationId;
    private String description;
    private String type; // Changed from Enum for flexibility
}