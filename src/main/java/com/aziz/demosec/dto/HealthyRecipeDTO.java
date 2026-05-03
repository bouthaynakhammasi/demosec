package com.aziz.demosec.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthyRecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String ingredients;
    private String instructions;
    private Integer calories;
    private Integer prepTimeMinutes;
    private String category;
    private String imageUrl;
    private Long nutritionistId;
    private String nutritionistName;
}
