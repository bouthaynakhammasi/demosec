package com.aziz.demosec.dto.donation;

import com.aziz.demosec.Entities.DonationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DonationRequestDTO {
    private String donorName;
    private DonationType type;       // MONEY ou MATERIEL
    private Double amount;           // si MONEY
    private String categorie;        // si MATERIEL
    private String description;      // si MATERIEL
    private Integer quantite;
}
