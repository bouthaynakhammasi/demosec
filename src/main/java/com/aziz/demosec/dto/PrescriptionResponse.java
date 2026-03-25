package com.aziz.demosec.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionResponse {

    private Long id;
    private Long consultationId;
    private LocalDate date;

    // Flat fields for UI compatibility
    private String medication;
    private String dosage;
    private String instructions;

    private List<PrescriptionItemResponse> items;
}