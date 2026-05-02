package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.DiagnosisType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResponse {

    private Long id;
    private Long consultationId;
    private String description;
    private DiagnosisType type;
}