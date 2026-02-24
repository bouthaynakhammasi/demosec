package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Entities.PrescriptionItem;
import com.aziz.demosec.dto.PrescriptionItemResponse;
import com.aziz.demosec.dto.PrescriptionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrescriptionMapper {

    public PrescriptionResponse toDto(Prescription prescription) {
        if (prescription == null) return null;

        List<PrescriptionItemResponse> itemResponses = new ArrayList<>();

        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                itemResponses.add(
                        PrescriptionItemResponse.builder()
                                .id(item.getId())
                                .medicationName(item.getMedicationName())
                                .dosage(item.getDosage())
                                .frequency(item.getFrequency())
                                .duration(item.getDuration())
                                .build()
                );
            }
        }

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .consultationId(
                        prescription.getConsultation() != null ? prescription.getConsultation().getId() : null
                )
                .date(prescription.getDate())
                .items(itemResponses)
                .build();
    }
}