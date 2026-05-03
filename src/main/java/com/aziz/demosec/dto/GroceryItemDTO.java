package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroceryItemDTO {
    private Long id;
    private Long patientId;
    private Long lifestylePlanId;
    private String itemName;
    private String quantity;
    private String unit;
    private boolean purchased;
    private String sourceRecipe;
    private java.time.LocalDateTime createdAt;
}
