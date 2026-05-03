package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ReceiveBatchRequest;
import com.aziz.demosec.dto.StockMovementRequest;
import com.aziz.demosec.dto.StockBatchResponse;
import com.aziz.demosec.dto.StockMovementResponse;
import com.aziz.demosec.dto.StockoutPredictionRequest;
import com.aziz.demosec.dto.StockoutPredictionResult;
import com.aziz.demosec.service.IStockService;
import com.aziz.demosec.service.StockoutPredictionClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final IStockService stockService;
    private final StockoutPredictionClient stockoutPredictionClient;

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

    @GetMapping("/pharmacy/{pharmacyId}")
    public java.util.List<com.aziz.demosec.dto.PharmacyStockResponse> listStocksByPharmacy(@PathVariable Long pharmacyId) {
        return stockService.listStocksByPharmacy(pharmacyId);
    }

    @GetMapping("/{stockId}/batches")
    public java.util.List<com.aziz.demosec.dto.StockBatchResponse> listBatches(@PathVariable Long stockId) {
        return stockService.listBatches(stockId);
    }

    @GetMapping("/{stockId}/movements")
    public java.util.List<com.aziz.demosec.dto.StockMovementResponse> listMovements(@PathVariable Long stockId) {
        return stockService.listMovements(stockId);
    }

    @GetMapping("/alerts/open")
    public java.util.List<com.aziz.demosec.dto.StockAlertResponse> listOpenAlerts() {
        return stockService.listOpenAlerts();
    }

    @GetMapping("/{stockId}/alerts/open")
    public java.util.List<com.aziz.demosec.dto.StockAlertResponse> listOpenAlertsByStock(@PathVariable Long stockId) {
        return stockService.listOpenAlertsByStock(stockId);
    }

    @GetMapping("/summary")
    public java.util.List<com.aziz.demosec.dto.StockSummaryResponse> getStockSummary() {
        return stockService.getStockSummary();
    }

    @GetMapping("/search")
    public org.springframework.data.domain.Page<com.aziz.demosec.dto.PharmacyStockResponse> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return stockService.searchProducts(keyword, org.springframework.data.domain.PageRequest.of(page, size));
    }

    @GetMapping("/pharmacy/{pharmacyId}/replenishment-predictions")
    public java.util.List<com.aziz.demosec.dto.ReplenishmentPredictionResponse> predictReplenishment(@PathVariable Long pharmacyId) {
        return stockService.predictReplenishment(pharmacyId);
    }

    @GetMapping("/pharmacy/{pharmacyId}/expiration-risks")
    public java.util.List<com.aziz.demosec.dto.ExpirationRiskResponse> getExpirationRiskDashboard(@PathVariable Long pharmacyId) {
        return stockService.getExpirationRiskDashboard(pharmacyId);
    }

    @PostMapping("/ml-predict")
    public ResponseEntity<?> mlPredict(@RequestBody StockoutPredictionRequest request) {
        StockoutPredictionResult result = stockoutPredictionClient.predict(request);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("ML service is unavailable. Make sure the Python service is running on port 8000.");
        }
        return ResponseEntity.ok(result);
    }
}