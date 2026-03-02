package com.aziz.demosec.service;

import com.aziz.demosec.dto.request.ReceiveBatchRequest;
import com.aziz.demosec.dto.request.StockMovementRequest;
import com.aziz.demosec.dto.response.PharmacyStockResponse;
import com.aziz.demosec.dto.response.StockAlertResponse;
import com.aziz.demosec.dto.response.StockBatchResponse;
import com.aziz.demosec.dto.response.StockMovementResponse;

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