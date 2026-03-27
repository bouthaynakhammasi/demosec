package com.aziz.demosec.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionRequest {

    private Long consultationId;
    private String date; // String for consistency

    // Flat payload support
    private String medication;
    private String dosage;
    private String instructions;

    // Nested payload support
    private List<PrescriptionItemRequest> items;
}