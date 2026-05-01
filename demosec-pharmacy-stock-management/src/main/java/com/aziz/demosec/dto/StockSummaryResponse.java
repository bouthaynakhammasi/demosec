package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSummaryResponse {
    private String pharmacyName;
    private String productName;
    private Integer totalStock;
    private Long numberOfBatches;
    private LocalDate nearestExpirationDate;
}
