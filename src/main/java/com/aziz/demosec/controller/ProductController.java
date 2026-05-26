package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.ProductType;
import com.aziz.demosec.dto.pharmacy.ProductRequestDTO;
import com.aziz.demosec.dto.pharmacy.ProductResponseDTO;
import com.aziz.demosec.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> search(@RequestParam("name") String name) {
        return ResponseEntity.ok(productService.search(name));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProductResponseDTO>> getByType(@PathVariable("type") String type) {
        ProductType productType = ProductType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(productService.getByType(productType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable("id") Long id,
                                                     @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

