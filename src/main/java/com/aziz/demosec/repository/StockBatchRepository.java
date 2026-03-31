package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
    List<StockBatch> findByPharmacyStockIdOrderByExpirationDateAsc(Long pharmacyStockId);
    List<StockBatch> findByExpirationDateBefore(LocalDate date);
    List<StockBatch> findByExpirationDateBetween(LocalDate start, LocalDate end);

}