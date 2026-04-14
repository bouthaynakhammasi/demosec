package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.PharmacyStockRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;

import java.util.List;

public interface PharmacyStockService {
    PharmacyStockResponseDTO create(PharmacyStockRequestDTO dto);
    PharmacyStockResponseDTO getById(Long id);
    List<PharmacyStockResponseDTO> getByPharmacy(Long pharmacyId);
    List<PharmacyStockResponseDTO> findPharmaciesWithProduct(Long productId);
    PharmacyStockResponseDTO update(Long id, PharmacyStockRequestDTO dto);
    void delete(Long id);
}
