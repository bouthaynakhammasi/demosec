package com.aziz.demosec.repository;

import com.aziz.demosec.entities.PharmacyStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PharmacyStockRepository extends JpaRepository<PharmacyStock, Long> {
    Optional<PharmacyStock> findByPharmacyIdAndProductId(Long pharmacyId, Long productId);
    List<PharmacyStock> findByPharmacyId(Long pharmacyId);

}
