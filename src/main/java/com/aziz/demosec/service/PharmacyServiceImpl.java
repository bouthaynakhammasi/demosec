package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Pharmacy;
import com.aziz.demosec.dto.pharmacy.PharmacyRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyResponseDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.repository.PharmacyRepository;
import com.aziz.demosec.repository.PharmacyStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyServiceImpl implements IPharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyStockRepository stockRepository;

    @Override
    public PharmacyResponseDTO create(PharmacyRequestDTO dto) {
        Pharmacy p = new Pharmacy();
        return toDTO(pharmacyRepository.save(map(p, dto)));
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponseDTO> getAll() {
        return pharmacyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponseDTO> search(String name) {
        return pharmacyRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PharmacyResponseDTO update(Long id, PharmacyRequestDTO dto) {
        return toDTO(pharmacyRepository.save(map(findOrThrow(id), dto)));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        pharmacyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyStockResponseDTO> searchByProduct(Long productId, String city, int minQty) {
        return stockRepository.findByProduct_IdAndTotalQuantityGreaterThan(productId, minQty).stream()
                .filter(s -> city == null || city.isBlank() ||
                        (s.getPharmacy().getAddress() != null &&
                         s.getPharmacy().getAddress().toLowerCase().contains(city.toLowerCase())))
                .map(s -> PharmacyStockResponseDTO.builder()
                        .id(s.getId())
                        .pharmacyId(s.getPharmacy().getId())
                        .pharmacyName(s.getPharmacy().getName())
                        .productId(s.getProduct().getId())
                        .productName(s.getProduct().getName())
                        .totalQuantity(s.getTotalQuantity())
                        .minQuantityThreshold(s.getMinQuantityThreshold())
                        .unitPrice(s.getUnitPrice())
                        .stockStatus(s.getTotalQuantity() <= s.getMinQuantityThreshold() ? "LOW" : "OK")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyStockResponseDTO> searchByProducts(List<Long> productIds, int minQty) {
        return stockRepository.findByProduct_IdInAndTotalQuantityGreaterThan(productIds, minQty).stream()
                .map(s -> PharmacyStockResponseDTO.builder()
                        .id(s.getId())
                        .pharmacyId(s.getPharmacy().getId())
                        .pharmacyName(s.getPharmacy().getName())
                        .productId(s.getProduct().getId())
                        .productName(s.getProduct().getName())
                        .totalQuantity(s.getTotalQuantity())
                        .minQuantityThreshold(s.getMinQuantityThreshold())
                        .unitPrice(s.getUnitPrice())
                        .stockStatus(s.getTotalQuantity() <= s.getMinQuantityThreshold() ? "LOW" : "OK")
                        .build())
                .collect(Collectors.toList());
    }

    private Pharmacy findOrThrow(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found: " + id));
    }

    private Pharmacy map(Pharmacy p, PharmacyRequestDTO dto) {
        p.setName(dto.getName());
        p.setAddress(dto.getAddress());
        p.setLocationLat(dto.getLocationLat());
        p.setLocationLng(dto.getLocationLng());
        p.setPhoneNumber(dto.getPhoneNumber());
        p.setEmail(dto.getEmail());
        return p;
    }

    private PharmacyResponseDTO toDTO(Pharmacy p) {
        return PharmacyResponseDTO.builder()
                .id(p.getId()).name(p.getName()).address(p.getAddress())
                .locationLat(p.getLocationLat()).locationLng(p.getLocationLng())
                .phoneNumber(p.getPhoneNumber()).email(p.getEmail())
                .build();
    }
}
