package com.aziz.demosec.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StockBatchResponse {
    private Long id;
    private Long pharmacyStockId;

    private String batchNumber;
    private Integer quantity;
    private LocalDate expirationDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    private LocalDateTime receivedAt;
}