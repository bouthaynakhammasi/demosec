package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.ProductRequestDTO;
import com.aziz.demosec.dto.pharmacy.ProductResponseDTO;
import com.aziz.demosec.Entities.ProductType;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO dto);
    ProductResponseDTO getById(Long id);
    List<ProductResponseDTO> getAll();
    List<ProductResponseDTO> search(String name);
    List<ProductResponseDTO> getByType(ProductType type);
    ProductResponseDTO update(Long id, ProductRequestDTO dto);
    void delete(Long id);
}
