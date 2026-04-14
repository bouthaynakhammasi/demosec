package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.PharmacyStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PharmacyStockRepository extends JpaRepository<PharmacyStock, Long> {

    List<PharmacyStock> findByPharmacy_Id(Long pharmacyId);

    Optional<PharmacyStock> findByPharmacy_IdAndProduct_Id(Long pharmacyId, Long productId);

    List<PharmacyStock> findByProduct_IdAndTotalQuantityGreaterThan(Long productId, int minQty);

    List<PharmacyStock> findByProduct_IdInAndTotalQuantityGreaterThan(List<Long> productIds, int minQty);
}
