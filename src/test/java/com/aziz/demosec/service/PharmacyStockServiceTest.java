package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Pharmacy;
import com.aziz.demosec.Entities.PharmacyStock;
import com.aziz.demosec.Entities.Product;
import com.aziz.demosec.dto.pharmacy.PharmacyStockRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.repository.PharmacyRepository;
import com.aziz.demosec.repository.PharmacyStockRepository;
import com.aziz.demosec.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmacyStockServiceTest {

    @Mock
    private PharmacyStockRepository stockRepository;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PharmacyStockServiceImpl stockService;

    private Pharmacy pharmacy;
    private Product product;
    private PharmacyStock stock;
    private PharmacyStockRequestDTO stockRequestDTO;

    @BeforeEach
    void setUp() {
        pharmacy = Pharmacy.builder().id(1L).name("Test Pharmacy").build();
        product = new Product();
        product.setId(2L);
        product.setName("Test Product");

        stock = new PharmacyStock();
        stock.setId(10L);
        stock.setPharmacy(pharmacy);
        stock.setProduct(product);
        stock.setTotalQuantity(100);
        stock.setMinQuantityThreshold(10);
        stock.setUnitPrice(BigDecimal.valueOf(25.5));

        stockRequestDTO = PharmacyStockRequestDTO.builder()
                .pharmacyId(1L)
                .productId(2L)
                .totalQuantity(100)
                .minQuantityThreshold(10)
                .unitPrice(BigDecimal.valueOf(25.5))
                .build();
    }

    @Test
    void create_ShouldSaveAndReturnStock() {
        // Arrange
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(stockRepository.save(any(PharmacyStock.class))).thenReturn(stock);

        // Act
        PharmacyStockResponseDTO result = stockService.create(stockRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Pharmacy", result.getPharmacyName());
        verify(stockRepository, times(1)).save(any(PharmacyStock.class));
    }

    @Test
    void getById_ShouldReturnStock_WhenExists() {
        // Arrange
        when(stockRepository.findById(10L)).thenReturn(Optional.of(stock));

        // Act
        PharmacyStockResponseDTO result = stockService.getById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void findPharmaciesWithProduct_ShouldReturnList() {
        // Arrange
        when(stockRepository.findByProduct_IdAndTotalQuantityGreaterThan(2L, 0))
                .thenReturn(List.of(stock));

        // Act
        List<PharmacyStockResponseDTO> result = stockService.findPharmaciesWithProduct(2L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void update_ShouldModifyStock() {
        // Arrange
        when(stockRepository.findById(10L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(PharmacyStock.class))).thenAnswer(i -> i.getArgument(0));

        stockRequestDTO.setTotalQuantity(50);

        // Act
        PharmacyStockResponseDTO result = stockService.update(10L, stockRequestDTO);

        // Assert
        assertEquals(50, result.getTotalQuantity());
        verify(stockRepository, times(1)).save(stock);
    }
}
