package com.aziz.demosec.Entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Nutrients {
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fats;
}
