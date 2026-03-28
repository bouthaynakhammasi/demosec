package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TreatmentRequest {

    @NotNull(message = "Consultation ID is required")
    private Long consultationId;

    @NotBlank(message = "Treatment type is required")
    private String treatmentType; // Changed from Enum for flexibility

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Start date is required")
    private String startDate; // String to avoid Jackson deserialization edge cases

    private String endDate;

    @NotBlank(message = "Status is required")
    private String status; // Changed from Enum for flexibility
}