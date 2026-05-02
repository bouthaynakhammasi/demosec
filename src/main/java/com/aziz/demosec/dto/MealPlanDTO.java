package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class MealPlanDTO {
    private Long id;
    private Long lifestylePlanId;
    private String lifestylePlanTitle;
    private Long recipeId;
    private String recipeTitle;
    private Integer recipeCalories;
    private Long patientId;
    private String patientName;
    private String dayOfWeek;
    private String mealType;
    private Integer weekNumber;
    private String notes;
}