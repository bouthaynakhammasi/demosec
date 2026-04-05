package com.aziz.demosec.dto.baby;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaperRecordDTO {
    private Long id;
    private Long babyId;

    @NotBlank(message = "Le type de couche est obligatoire")
    @Pattern(regexp = "WET|DIRTY|MIXED", message = "Type de couche invalide")
    private String diaperType; // WET, DIRTY, MIXED
    
    private boolean rashNoted;

    @Size(max = 50, message = "La couleur des selles est trop longue")
    private String stoolColor;

    @Size(max = 50, message = "La texture des selles est trop longue")
    private String stoolTexture;

    @Size(max = 500, message = "Les notes ne peuvent dépasser 500 caractères")
    private String notes;

    private LocalDateTime changedAt;
}
