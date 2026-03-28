package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.PharmacyRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyResponseDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;

import java.util.List;

public interface PharmacyService {
    PharmacyResponseDTO create(PharmacyRequestDTO dto);
    PharmacyResponseDTO getById(Long id);
    List<PharmacyResponseDTO> getAll();
    List<PharmacyResponseDTO> search(String name);
    List<PharmacyStockResponseDTO> searchByProduct(Long productId, String city, int minQty);
    List<PharmacyStockResponseDTO> searchByProducts(List<Long> productIds, int minQty);
    PharmacyResponseDTO update(Long id, PharmacyRequestDTO dto);
    void delete(Long id);
}
