package com.aziz.demosec.service;

import com.aziz.demosec.dto.pharmacy.ProductRequestDTO;
import com.aziz.demosec.dto.pharmacy.ProductResponseDTO;
import com.aziz.demosec.Entities.Product;
import com.aziz.demosec.Entities.ProductType;
import com.aziz.demosec.Entities.ProductUnit;
import com.aziz.demosec.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

 
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
        return convertToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> search(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .brand(dto.getBrand())
                .manufacturer(dto.getManufacturer())
                .barcode(dto.getBarcode())
                .imageUrl(dto.getImageUrl())
                .type(dto.getType() != null ? dto.getType().name() : null)
                .unit(dto.getUnit() != null ? dto.getUnit().name() : null)
                .build();
        
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getByType(ProductType type) {
        return productRepository.findByType(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setManufacturer(dto.getManufacturer());
        product.setBarcode(dto.getBarcode());
        product.setImageUrl(dto.getImageUrl());
        product.setType(dto.getType() != null ? dto.getType().name() : null);
        product.setUnit(dto.getUnit() != null ? dto.getUnit().name() : null);

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDTO convertToDTO(Product product) {
        ProductType type = null;
        ProductUnit unit = null;
        
        if (product.getType() != null) {
            try {
                type = ProductType.valueOf(product.getType());
            } catch (IllegalArgumentException e) {
                // Log ou ignorer
            }
        }
        
        if (product.getUnit() != null) {
            try {
                unit = ProductUnit.valueOf(product.getUnit());
            } catch (IllegalArgumentException e) {
                // Log ou ignorer
            }
        }
        
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .brand(product.getBrand())
                .manufacturer(product.getManufacturer())
                .barcode(product.getBarcode())
                .imageUrl(product.getImageUrl())
                .type(type)
                .unit(unit)
                .build();
    }
}
