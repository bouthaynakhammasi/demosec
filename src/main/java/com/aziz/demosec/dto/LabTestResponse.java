package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.TestType;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LabTestResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long laboratoryId;
    private String laboratoryName;
    private String category;
    private TestType testType;
    private Integer durationMinutes;
    private String genderSpecific;
    private Boolean requiresAppointment;
    private Boolean requiresFasting;
}