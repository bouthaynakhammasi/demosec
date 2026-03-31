package com.aziz.demosec.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

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
    private BigDecimal unitPrice;
    private String stockStatus; // "OK" or "LOW"
}

