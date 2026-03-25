package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TreatmentRequest {

    private Long consultationId;

    private String treatmentType; // Changed from Enum for flexibility
    private String description;

    private String startDate; // String to avoid Jackson deserialization edge cases
    private String endDate;

    private String status; // Changed from Enum for flexibility
}