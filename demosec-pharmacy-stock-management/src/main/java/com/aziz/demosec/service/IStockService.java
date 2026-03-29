package com.aziz.demosec.service;

import com.aziz.demosec.dto.ReceiveBatchRequest;
import com.aziz.demosec.dto.StockMovementRequest;
import com.aziz.demosec.dto.PharmacyStockResponse;
import com.aziz.demosec.dto.StockAlertResponse;
import com.aziz.demosec.dto.StockBatchResponse;
import com.aziz.demosec.dto.StockMovementResponse;

import java.util.List;

public interface IStockService {
    PharmacyStockResponse getOrCreateStock(Long pharmacyId, Long productId, Integer minThreshold);

    StockBatchResponse receiveBatch(ReceiveBatchRequest request);
    StockMovementResponse createMovement(StockMovementRequest request);

    List<PharmacyStockResponse> listStocksByPharmacy(Long pharmacyId);
    List<StockBatchResponse> listBatches(Long pharmacyStockId);
    List<StockMovementResponse> listMovements(Long pharmacyStockId);

    List<StockAlertResponse> listOpenAlerts();
    List<StockAlertResponse> listOpenAlertsByStock(Long pharmacyStockId);
    StockAlertResponse resolveAlert(Long alertId);
}