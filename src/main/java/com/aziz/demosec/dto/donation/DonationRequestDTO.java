package com.aziz.demosec.dto.donation;

import com.aziz.demosec.Entities.DonationType;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequestDTO {
    
    @NotBlank(message = "Le nom du donneur est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom du donneur doit comporter entre 2 et 50 caractères")
    private String donorName;
    
    @NotNull(message = "Le type de donation est obligatoire (MONEY ou MATERIEL)")
    private DonationType type;       // MONEY ou MATERIEL
    
    @Positive(message = "Le montant doit être strictement positif")
    private Double amount;           // si MONEY
    
    private String categorie;        // si MATERIEL
    private String description;      // si MATERIEL
    
    @Positive(message = "La quantité doit être strictement positive")
    private Integer quantite;
    
    @NotNull(message = "L'ID du créateur est obligatoire")
    private Long creatorId;

    // Image in Base64
    private String imageData;
}
