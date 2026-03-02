package com.aziz.demosec.controller;

import com.aziz.demosec.dto.request.ReceiveBatchRequest;
import com.aziz.demosec.dto.request.StockMovementRequest;
import com.aziz.demosec.dto.response.*;
import com.aziz.demosec.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final IStockService stockService;

    @PostMapping("/batches/receive")
    @ResponseStatus(HttpStatus.CREATED)
    public StockBatchResponse receiveBatch(@RequestBody ReceiveBatchRequest request) {
        return stockService.receiveBatch(request);
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse createMovement(@RequestBody StockMovementRequest request) {
        return stockService.createMovement(request);
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public List<PharmacyStockResponse> listStocksByPharmacy(@PathVariable Long pharmacyId) {
        return stockService.listStocksByPharmacy(pharmacyId);
    }

    @GetMapping("/{pharmacyStockId}/batches")
    public List<StockBatchResponse> listBatches(@PathVariable Long pharmacyStockId) {
        return stockService.listBatches(pharmacyStockId);
    }

    @GetMapping("/{pharmacyStockId}/movements")
    public List<StockMovementResponse> listMovements(@PathVariable Long pharmacyStockId) {
        return stockService.listMovements(pharmacyStockId);
    }

    @GetMapping("/alerts/open")
    public List<StockAlertResponse> listOpenAlerts() {
        return stockService.listOpenAlerts();
    }

    @GetMapping("/{pharmacyStockId}/alerts/open")
    public List<StockAlertResponse> listOpenAlertsByStock(@PathVariable Long pharmacyStockId) {
        return stockService.listOpenAlertsByStock(pharmacyStockId);
    }

    @PatchMapping("/alerts/{alertId}/resolve")
    public StockAlertResponse resolveAlert(@PathVariable Long alertId) {
        return stockService.resolveAlert(alertId);
    }
}