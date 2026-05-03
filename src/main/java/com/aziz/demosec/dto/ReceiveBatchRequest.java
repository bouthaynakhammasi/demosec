package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiveBatchRequest {

    @NotNull(message = "Pharmacy ID is required")
    @Positive(message = "Pharmacy ID must be positive")
    private Long pharmacyId;

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @FutureOrPresent(message = "Expiration date must be today or in the future")
    private LocalDate expirationDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    private BigDecimal sellingPrice;

    @PositiveOrZero(message = "Min quantity threshold must be >= 0")
    private Integer minQuantityThreshold;
}