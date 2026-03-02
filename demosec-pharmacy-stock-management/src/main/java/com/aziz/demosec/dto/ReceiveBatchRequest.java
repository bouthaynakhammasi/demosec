package com.aziz.demosec.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiveBatchRequest {
    private Long pharmacyId;
    private Long productId;

    private String batchNumber;
    private Integer quantity;

    private LocalDate expirationDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    // optionnel: si stock n’existe pas encore
    private Integer minQuantityThreshold;
}