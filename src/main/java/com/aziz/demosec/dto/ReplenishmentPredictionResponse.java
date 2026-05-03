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
public class ReplenishmentPredictionResponse {
    private Long pharmacyStockId;
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer consumptionLast30Days;
    private Integer activeAlerts;
    private Boolean isSeasonal;
    private Integer suggestedOrderQuantity;
    private LocalDate estimatedDepletionDate;

    // ML model fields (null when Python service is unavailable)
    private Boolean willStockout;
    private Double stockoutProbability;
    private String mlBadge;
    private Double mlDaysUntilStockout;
}
