package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.dto.ConsultationResponse;
import org.springframework.stereotype.Component;

@Component
public class ConsultationMapper {

    public ConsultationResponse toDto(Consultation consultation) {
        if (consultation == null) return null;

        return ConsultationResponse.builder()
                .id(consultation.getId())
                .medicalRecordId(
                        consultation.getMedicalRecord() != null ?
                                consultation.getMedicalRecord().getId() : null
                )
                .doctorId(
                        consultation.getDoctor() != null ?
                                consultation.getDoctor().getId() : null
                )
                .date(consultation.getDate())
                .observations(consultation.getObservations())
                .notes(consultation.getNotes())
                .build();
    }
}