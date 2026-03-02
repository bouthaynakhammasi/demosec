package com.aziz.demosec.dto.request;

import com.aziz.demosec.entities.StockMovementType;
import lombok.Data;

@Data
public class StockMovementRequest {
    private Long pharmacyStockId;
    private StockMovementType movementType;
    private Integer quantity;
    private String reference;
}