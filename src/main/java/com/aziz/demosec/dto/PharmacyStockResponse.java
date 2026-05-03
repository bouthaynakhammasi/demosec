package com.aziz.demosec.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PharmacyStockResponse {
    private Long id;
    private Long pharmacyId;
    private String pharmacyName;

    private Long productId;
    private String productName;

    private Integer totalQuantity;
    private Integer minQuantityThreshold;
}