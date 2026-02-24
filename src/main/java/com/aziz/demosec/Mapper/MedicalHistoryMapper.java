package com.aziz.demosec.Mapper;


import com.aziz.demosec.Entities.MedicalHistory;
import com.aziz.demosec.dto.MedicalHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryMapper {

    public MedicalHistoryResponse toDto(MedicalHistory history) {
        if (history == null) return null;

        return MedicalHistoryResponse.builder()
                .id(history.getId())
                .medicalRecordId(
                        history.getMedicalRecord() != null ? history.getMedicalRecord().getId() : null
                )
                .type(history.getType())
                .description(history.getDescription())
                .startDate(history.getStartDate())
                .endDate(history.getEndDate())
                .status(history.getStatus())
                .build();
    }
}