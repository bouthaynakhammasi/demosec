package com.aziz.demosec.service;

import com.aziz.demosec.Entities.PharmacyStock;
import com.aziz.demosec.dto.pharmacy.PharmacyStockRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.repository.PharmacyRepository;
import com.aziz.demosec.repository.PharmacyStockRepository;
import com.aziz.demosec.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyStockServiceImpl implements PharmacyStockService {

    private final PharmacyStockRepository stockRepository;
    private final PharmacyRepository pharmacyRepository;
    private final ProductRepository productRepository;

    @Override
    public PharmacyStockResponseDTO create(PharmacyStockRequestDTO dto) {
        PharmacyStock stock = new PharmacyStock();
        stock.setPharmacy(pharmacyRepository.findById(dto.getPharmacyId())
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found")));
        stock.setProduct(productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found")));
        stock.setTotalQuantity(dto.getTotalQuantity());
        stock.setMinQuantityThreshold(dto.getMinQuantityThreshold());
        stock.setUnitPrice(dto.getUnitPrice());
        return toDTO(stockRepository.save(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyStockResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyStockResponseDTO> getByPharmacy(Long pharmacyId) {
        return stockRepository.findByPharmacy_Id(pharmacyId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyStockResponseDTO> findPharmaciesWithProduct(Long productId) {
        return stockRepository.findByProduct_IdAndTotalQuantityGreaterThan(productId, 0)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PharmacyStockResponseDTO update(Long id, PharmacyStockRequestDTO dto) {
        PharmacyStock stock = findOrThrow(id);
        stock.setTotalQuantity(dto.getTotalQuantity());
        stock.setMinQuantityThreshold(dto.getMinQuantityThreshold());
        stock.setUnitPrice(dto.getUnitPrice());
        return toDTO(stockRepository.save(stock));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        stockRepository.deleteById(id);
    }

    private PharmacyStock findOrThrow(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + id));
    }

    private PharmacyStockResponseDTO toDTO(PharmacyStock s) {
        return PharmacyStockResponseDTO.builder()
                .id(s.getId())
                .pharmacyId(s.getPharmacy().getId()).pharmacyName(s.getPharmacy().getName())
                .productId(s.getProduct().getId()).productName(s.getProduct().getName())
                .totalQuantity(s.getTotalQuantity()).minQuantityThreshold(s.getMinQuantityThreshold())
                .unitPrice(s.getUnitPrice())
                .stockStatus(s.getTotalQuantity() <= s.getMinQuantityThreshold() ? "LOW" : "OK")
                .build();
    }
}
