package com.aziz.demosec.repository;

import com.aziz.demosec.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByPharmacyStockIdOrderByCreatedAtDesc(Long pharmacyStockId);
}