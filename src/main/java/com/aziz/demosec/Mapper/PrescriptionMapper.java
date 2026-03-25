package com.aziz.demosec.Mapper;

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
        String medication = "";
        String dosage = "";
        String instructions = "";

        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            // Map all items to the list
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

            // Map first item to flat fields for frontend compatibility
            PrescriptionItem first = prescription.getItems().get(0);
            medication = first.getMedicationName();
            dosage = first.getDosage();
            instructions = first.getDuration(); // We map duration to instructions in this UI
        }

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .consultationId(
                        prescription.getConsultation() != null ? prescription.getConsultation().getId() : null
                )
                .date(prescription.getDate())
                .medication(medication)
                .dosage(dosage)
                .instructions(instructions)
                .items(itemResponses)
                .build();
    }
}