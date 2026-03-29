package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ReceiveBatchRequest;
import com.aziz.demosec.dto.StockMovementRequest;
import com.aziz.demosec.dto.StockBatchResponse;
import com.aziz.demosec.dto.StockMovementResponse;
import com.aziz.demosec.service.IStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final IStockService stockService;

    @PostMapping("/batches/receive")
    @ResponseStatus(HttpStatus.CREATED)
    public StockBatchResponse receiveBatch(@Valid @RequestBody ReceiveBatchRequest request) {
        return stockService.receiveBatch(request);
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse createMovement(@Valid @RequestBody StockMovementRequest request) {
        return stockService.createMovement(request);
    }

    @PatchMapping("/alerts/{id}/resolve")
    public void resolveAlert(@PathVariable Long id) {
        stockService.resolveAlert(id);
    }
}