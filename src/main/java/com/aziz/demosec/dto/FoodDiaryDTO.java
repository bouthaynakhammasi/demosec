package com.aziz.demosec.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodDiaryDTO {
    private Long id;
    private Long patientId;
    private Long lifestylePlanId;
    private LocalDate date;
    private String mealType;
    private String foodName;
    private Integer calories;
    private String notes;
    private String imageUrl;
}
