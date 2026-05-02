package com.aziz.demosec.dto.pharmacy;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyStockResponseDTO {

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
