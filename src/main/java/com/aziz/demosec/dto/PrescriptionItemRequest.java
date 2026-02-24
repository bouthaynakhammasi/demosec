package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionItemRequest {

    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
}