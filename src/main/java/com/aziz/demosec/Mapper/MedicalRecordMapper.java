package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.dto.MedicalRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    public MedicalRecordResponse toDto(MedicalRecord record) {
        if (record == null) return null;

        return MedicalRecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatient() != null ? record.getPatient().getId() : null)
                .build();
    }
}