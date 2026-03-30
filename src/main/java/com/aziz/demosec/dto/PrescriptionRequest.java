package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionRequest {

    @NotNull(message = "Consultation ID is required")
    private Long consultationId;

    @NotNull(message = "Date is required")
    private String date; // String for consistency

    // Flat payload support
    private String medication;

    private String dosage;

    private String instructions;

    // Nested payload support
    private List<PrescriptionItemRequest> items;
}