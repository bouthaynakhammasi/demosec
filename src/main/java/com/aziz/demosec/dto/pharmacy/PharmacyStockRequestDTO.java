package com.aziz.demosec.dto.pharmacy;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyStockRequestDTO {

    @NotNull
    private Long pharmacyId;

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 0)
    private Integer totalQuantity;

    @NotNull
    @Min(value = 0)
    private Integer minQuantityThreshold;

    @NotNull
    @DecimalMin(value = "0.001")
    private BigDecimal unitPrice;
}
