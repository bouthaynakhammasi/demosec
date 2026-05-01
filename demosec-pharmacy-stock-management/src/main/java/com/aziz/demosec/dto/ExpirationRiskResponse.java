package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpirationRiskResponse {
    private Long batchId;
    private String productName;
    private String batchNumber;
    private Integer quantity;
    private LocalDate expirationDate;
    
    // Risk zones: EXPIRED, RED (Critical), ORANGE (Warning), GREEN (Safe)
    private String riskLevel;
    
    // Suggests what to do: 'Destruction', 'Retour fournisseur', 'Promotion', etc.
    private String recommendedAction;
}
