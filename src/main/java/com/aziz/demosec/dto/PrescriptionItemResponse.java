package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionItemResponse {

    private Long id;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
}