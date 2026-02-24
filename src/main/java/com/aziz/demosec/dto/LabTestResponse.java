package com.aziz.demosec.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long laboratoryId;
    private String laboratoryName;
}