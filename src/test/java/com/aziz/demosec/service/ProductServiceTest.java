package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Product;
import com.aziz.demosec.Entities.ProductType;
import com.aziz.demosec.dto.pharmacy.ProductRequestDTO;
import com.aziz.demosec.dto.pharmacy.ProductResponseDTO;
import com.aziz.demosec.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Doliprane")
                .description("Paracetamol-based painkiller")
                .type("MEDICATION")
                .unit("BOX")
                .build();

        productRequestDTO = ProductRequestDTO.builder()
                .name("Doliprane")
                .description("Paracetamol-based painkiller")
                .type("MEDICATION")
                .unit("BOX")
                .build();
    }

    @Test
    void create_ShouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDTO result = productService.create(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Doliprane", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getById_ShouldReturnProduct_WhenExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = productService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void search_ShouldReturnMatchingProducts() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("doli")).thenReturn(List.of(product));

        // Act
        List<ProductResponseDTO> results = productService.search("doli");

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Doliprane", results.get(0).getName());
    }

    @Test
    void getByType_ShouldReturnTypedProducts() {
        // Arrange
        when(productRepository.findByType(ProductType.MEDICATION)).thenReturn(List.of(product));

        // Act
        List<ProductResponseDTO> results = productService.getByType(ProductType.MEDICATION);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(ProductType.MEDICATION.name(), results.get(0).getType());
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        productService.delete(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }
}
