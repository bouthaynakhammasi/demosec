package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.PharmacyStockRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.service.IPharmacyStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/stocks")
@RequiredArgsConstructor
public class PharmacyStockController {

    private final IPharmacyStockService stockService;

    @PostMapping
    public ResponseEntity<PharmacyStockResponseDTO> create(@Valid @RequestBody PharmacyStockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyStockResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(stockService.getById(id));
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<PharmacyStockResponseDTO>> getByPharmacy(@PathVariable("pharmacyId") Long pharmacyId) {
        return ResponseEntity.ok(stockService.getByPharmacy(pharmacyId));
    }

    @GetMapping("/availability/{productId}")
    public ResponseEntity<List<PharmacyStockResponseDTO>> findAvailability(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(stockService.findPharmaciesWithProduct(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PharmacyStockResponseDTO> update(@PathVariable("id") Long id,
                                                           @Valid @RequestBody PharmacyStockRequestDTO dto) {
        return ResponseEntity.ok(stockService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        stockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
