package com.aziz.demosec.service;

import com.aziz.demosec.dto.ReceiveBatchRequest;
import com.aziz.demosec.dto.StockMovementRequest;
import com.aziz.demosec.dto.PharmacyStockResponse;
import com.aziz.demosec.dto.StockAlertResponse;
import com.aziz.demosec.dto.StockBatchResponse;
import com.aziz.demosec.dto.StockMovementResponse;
import com.aziz.demosec.entities.*;
import com.aziz.demosec.repository.PharmacyRepository;
import com.aziz.demosec.repository.ProductRepository;
import com.aziz.demosec.repository.PharmacyStockRepository;
import com.aziz.demosec.repository.StockBatchRepository;
import com.aziz.demosec.repository.StockMovementRepository;
import com.aziz.demosec.repository.StockAlertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements IStockService {

    private final PharmacyRepository pharmacyRepository;
    private final ProductRepository productRepository;
    private final PharmacyStockRepository pharmacyStockRepository;
    private final StockBatchRepository stockBatchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockAlertRepository stockAlertRepository;

    @Override
    public PharmacyStockResponse getOrCreateStock(Long pharmacyId, Long productId, Integer minThreshold) {
        PharmacyStock ps = pharmacyStockRepository.findByPharmacyIdAndProductId(pharmacyId, productId)
                .orElseGet(() -> {
                    Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                            .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found: " + pharmacyId));
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

                    PharmacyStock created = PharmacyStock.builder()
                            .pharmacy(pharmacy)
                            .product(product)
                            .totalQuantity(0)
                            .minQuantityThreshold(minThreshold != null ? minThreshold : 0)
                            .build();

                    return pharmacyStockRepository.save(created);
                });

        return toStockResponse(ps);
    }

    @Override
    public StockBatchResponse receiveBatch(ReceiveBatchRequest request) {
        PharmacyStock ps = pharmacyStockRepository.findByPharmacyIdAndProductId(request.getPharmacyId(), request.getProductId())
                .orElseGet(() -> {
                    // create stock if not exists
                    return fromResponse(getOrCreateStock(request.getPharmacyId(), request.getProductId(), request.getMinQuantityThreshold()));
                });

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        StockBatch batch = StockBatch.builder()
                .pharmacyStock(ps)
                .batchNumber(request.getBatchNumber())
                .quantity(request.getQuantity())
                .expirationDate(request.getExpirationDate())
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .receivedAt(LocalDateTime.now())
                .build();

        StockBatch savedBatch = stockBatchRepository.save(batch);

        // update total stock
        ps.setTotalQuantity(ps.getTotalQuantity() + request.getQuantity());
        pharmacyStockRepository.save(ps);

        // movement IN
        StockMovement m = StockMovement.builder()
                .pharmacyStock(ps)
                .movementType(StockMovementType.IN)
                .quantity(request.getQuantity())
                .reference("BATCH:" + savedBatch.getId())
                .createdAt(LocalDateTime.now())
                .build();
        stockMovementRepository.save(m);

        refreshStockAlerts(ps);

        return toBatchResponse(savedBatch);
    }

    @Override
    public StockMovementResponse createMovement(StockMovementRequest request) {
        PharmacyStock ps = pharmacyStockRepository.findById(request.getPharmacyStockId())
                .orElseThrow(() -> new EntityNotFoundException("PharmacyStock not found: " + request.getPharmacyStockId()));

        int qty = request.getQuantity() != null ? request.getQuantity() : 0;
        if (qty <= 0) throw new IllegalArgumentException("quantity must be > 0");

        switch (request.getMovementType()) {
            case IN, RETURN -> ps.setTotalQuantity(ps.getTotalQuantity() + qty);
            case OUT, TRANSFER, ADJUSTMENT -> ps.setTotalQuantity(ps.getTotalQuantity() - qty);
            default -> throw new IllegalArgumentException("Unsupported movement type");
        }

        if (ps.getTotalQuantity() < 0) {
            throw new IllegalStateException("Stock cannot be negative");
        }

        pharmacyStockRepository.save(ps);

        StockMovement movement = StockMovement.builder()
                .pharmacyStock(ps)
                .movementType(request.getMovementType())
                .quantity(qty)
                .reference(request.getReference())
                .createdAt(LocalDateTime.now())
                .build();

        StockMovement saved = stockMovementRepository.save(movement);

        refreshStockAlerts(ps);

        return toMovementResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyStockResponse> listStocksByPharmacy(Long pharmacyId) {
        return pharmacyStockRepository.findByPharmacyId(pharmacyId)
                .stream().map(this::toStockResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockBatchResponse> listBatches(Long pharmacyStockId) {
        return stockBatchRepository.findByPharmacyStockIdOrderByExpirationDateAsc(pharmacyStockId)
                .stream().map(this::toBatchResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> listMovements(Long pharmacyStockId) {
        return stockMovementRepository.findByPharmacyStockIdOrderByCreatedAtDesc(pharmacyStockId)
                .stream().map(this::toMovementResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertResponse> listOpenAlerts() {
        return stockAlertRepository.findByResolvedFalseOrderByCreatedAtDesc()
                .stream().map(this::toAlertResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertResponse> listOpenAlertsByStock(Long pharmacyStockId) {
        return stockAlertRepository.findByPharmacyStockIdAndResolvedFalse(pharmacyStockId)
                .stream().map(this::toAlertResponse).toList();
    }

    @Override
    public StockAlertResponse resolveAlert(Long alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found: " + alertId));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return toAlertResponse(stockAlertRepository.save(alert));
    }

    // ------------------ Mapping helpers ------------------

    private PharmacyStockResponse toStockResponse(PharmacyStock ps) {
        return PharmacyStockResponse.builder()
                .id(ps.getId())
                .pharmacyId(ps.getPharmacy().getId())
                .pharmacyName(ps.getPharmacy().getName())
                .productId(ps.getProduct().getId())
                .productName(ps.getProduct().getName())
                .totalQuantity(ps.getTotalQuantity())
                .minQuantityThreshold(ps.getMinQuantityThreshold())
                .build();
    }

    private StockBatchResponse toBatchResponse(StockBatch b) {
        return StockBatchResponse.builder()
                .id(b.getId())
                .pharmacyStockId(b.getPharmacyStock().getId())
                .batchNumber(b.getBatchNumber())
                .quantity(b.getQuantity())
                .expirationDate(b.getExpirationDate())
                .purchasePrice(b.getPurchasePrice())
                .sellingPrice(b.getSellingPrice())
                .receivedAt(b.getReceivedAt())
                .build();
    }

    private StockMovementResponse toMovementResponse(StockMovement m) {
        return StockMovementResponse.builder()
                .id(m.getId())
                .pharmacyStockId(m.getPharmacyStock().getId())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .reference(m.getReference())
                .createdAt(m.getCreatedAt())
                .performedById(m.getPerformedBy() != null ? m.getPerformedBy().getId() : null)
                .build();
    }

    private StockAlertResponse toAlertResponse(StockAlert a) {
        return StockAlertResponse.builder()
                .id(a.getId())
                .pharmacyStockId(a.getPharmacyStock().getId())
                .alertType(a.getAlertType())
                .message(a.getMessage())
                .createdAt(a.getCreatedAt())
                .resolved(a.isResolved())
                .resolvedAt(a.getResolvedAt())
                .build();
    }

    // hack: convert response -> entity (utilisé juste pour getOrCreateStock flow)
    private PharmacyStock fromResponse(PharmacyStockResponse r) {
        // On recharge depuis DB pour éviter une entité "fake"
        return pharmacyStockRepository.findById(r.getId())
                .orElseThrow(() -> new EntityNotFoundException("PharmacyStock not found: " + r.getId()));
    }

    // ------------------ Alerts logic (basic) ------------------

    private void refreshStockAlerts(PharmacyStock ps) {
        int total = ps.getTotalQuantity() != null ? ps.getTotalQuantity() : 0;
        int min = ps.getMinQuantityThreshold() != null ? ps.getMinQuantityThreshold() : 0;

        if (total == 0) {
            createAlertIfNotExists(ps, StockAlertType.OUT_OF_STOCK, "Product is out of stock.");
        }
        if (total > 0 && total <= min) {
            createAlertIfNotExists(ps, StockAlertType.LOW_STOCK, "Stock is below or equal to threshold (" + min + ").");
        }

        // auto resolve
        if (total > 0) autoResolve(ps, StockAlertType.OUT_OF_STOCK);
        if (total > min) autoResolve(ps, StockAlertType.LOW_STOCK);
    }

    private void createAlertIfNotExists(PharmacyStock ps, StockAlertType type, String message) {
        if (!stockAlertRepository.existsByPharmacyStockIdAndAlertTypeAndResolvedFalse(ps.getId(), type)) {
            StockAlert alert = StockAlert.builder()
                    .pharmacyStock(ps)
                    .alertType(type)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .resolved(false)
                    .build();
            stockAlertRepository.save(alert);
        }
    }

    private void autoResolve(PharmacyStock ps, StockAlertType type) {
        stockAlertRepository.findByPharmacyStockIdAndResolvedFalse(ps.getId())
                .stream()
                .filter(a -> a.getAlertType() == type)
                .forEach(a -> {
                    a.setResolved(true);
                    a.setResolvedAt(LocalDateTime.now());
                    stockAlertRepository.save(a);
                });
    }
}
