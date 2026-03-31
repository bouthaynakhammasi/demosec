package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.PharmacyRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyResponseDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.service.IPharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final IPharmacyService pharmacyService;

    @PostMapping
    public ResponseEntity<PharmacyResponseDTO> create(@Valid @RequestBody PharmacyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pharmacyService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PharmacyResponseDTO>> getAll() {
        return ResponseEntity.ok(pharmacyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pharmacyService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PharmacyResponseDTO>> search(@RequestParam("name") String name) {
        return ResponseEntity.ok(pharmacyService.search(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PharmacyResponseDTO> update(@PathVariable("id") Long id,
                                                      @Valid @RequestBody PharmacyRequestDTO dto) {
        return ResponseEntity.ok(pharmacyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        pharmacyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/product")
    public ResponseEntity<List<PharmacyStockResponseDTO>> searchByProduct(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "minQty", defaultValue = "1") int minQty) {
        return ResponseEntity.ok(pharmacyService.searchByProduct(productId, city, minQty));
    }

    @GetMapping("/search/batch")
    public ResponseEntity<List<PharmacyStockResponseDTO>> searchByProducts(
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam(value = "minQty", defaultValue = "1") int minQty) {
        return ResponseEntity.ok(pharmacyService.searchByProducts(productIds, minQty));
    }
}
