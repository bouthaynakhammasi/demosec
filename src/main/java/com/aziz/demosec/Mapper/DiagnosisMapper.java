package com.aziz.demosec.Mapper;


import com.aziz.demosec.Entities.Diagnosis;
import com.aziz.demosec.dto.DiagnosisResponse;
import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper {

    public DiagnosisResponse toDto(Diagnosis diagnosis) {
        if (diagnosis == null) return null;

        return DiagnosisResponse.builder()
                .id(diagnosis.getId())
                .consultationId(
                        diagnosis.getConsultation() != null ?
                                diagnosis.getConsultation().getId() : null
                )
                .description(diagnosis.getDescription())
                .type(diagnosis.getType())
                .build();
    }
}