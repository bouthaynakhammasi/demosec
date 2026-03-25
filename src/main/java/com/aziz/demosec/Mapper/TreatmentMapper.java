package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.Treatment;
import com.aziz.demosec.dto.TreatmentResponse;
import org.springframework.stereotype.Component;

@Component
public class TreatmentMapper {

    public TreatmentResponse toDto(Treatment treatment) {
        if (treatment == null)
            return null;

        return TreatmentResponse.builder()
                .id(treatment.getId())
                .consultationId(
                        treatment.getConsultation() != null ? treatment.getConsultation().getId() : null)
                .treatmentType(treatment.getTreatmentType())
                .description(treatment.getDescription())
                .startDate(treatment.getStartDate())
                .endDate(treatment.getEndDate())
                .status(treatment.getStatus())
                .build();
    }
}