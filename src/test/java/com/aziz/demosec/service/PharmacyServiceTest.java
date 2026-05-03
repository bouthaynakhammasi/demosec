package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Pharmacy;
import com.aziz.demosec.Entities.PharmacyStock;
import com.aziz.demosec.Entities.Product;
import com.aziz.demosec.dto.pharmacy.PharmacyRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyResponseDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyStockResponseDTO;
import com.aziz.demosec.repository.PharmacyRepository;
import com.aziz.demosec.repository.PharmacyStockRepository;
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
class PharmacyServiceTest {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyStockRepository stockRepository;

    @InjectMocks
    private PharmacyServiceImpl pharmacyService;

    private Pharmacy pharmacy;
    private PharmacyRequestDTO pharmacyRequestDTO;

    @BeforeEach
    void setUp() {
        pharmacy = Pharmacy.builder()
                .id(1L)
                .name("Pharmacie Centrale")
                .address("Tunis, Centre Ville")
                .phoneNumber("71123456")
                .email("centrale@pharmacie.tn")
                .build();

        pharmacyRequestDTO = PharmacyRequestDTO.builder()
                .name("Pharmacie Centrale")
                .address("Tunis, Centre Ville")
                .phoneNumber("71123456")
                .email("centrale@pharmacie.tn")
                .build();
    }

    @Test
    void create_ShouldReturnPharmacyResponse() {
        // Arrange
        when(pharmacyRepository.save(any(Pharmacy.class))).thenReturn(pharmacy);

        // Act
        PharmacyResponseDTO result = pharmacyService.create(pharmacyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Pharmacie Centrale", result.getName());
        verify(pharmacyRepository, times(1)).save(any(Pharmacy.class));
    }

    @Test
    void getById_ShouldReturnPharmacy_WhenExists() {
        // Arrange
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));

        // Act
        PharmacyResponseDTO result = pharmacyService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> pharmacyService.getById(1L));
    }

    @Test
    void searchByProduct_ShouldFilterByCity() {
        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Paracetamol");

        PharmacyStock stock = new PharmacyStock();
        stock.setId(100L);
        stock.setPharmacy(pharmacy);
        stock.setProduct(product);
        stock.setTotalQuantity(50);
        stock.setUnitPrice(BigDecimal.valueOf(5.5));
        stock.setMinQuantityThreshold(10);

        when(stockRepository.findByProduct_IdAndTotalQuantityGreaterThan(10L, 5))
                .thenReturn(List.of(stock));

        // Act
        List<PharmacyStockResponseDTO> results = pharmacyService.searchByProduct(10L, "Tunis", 5);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals("Pharmacie Centrale", results.get(0).getPharmacyName());
        assertTrue(results.get(0).getPharmacyName().contains("Centrale"));
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        // Arrange
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));

        // Act
        pharmacyService.delete(1L);

        // Assert
        verify(pharmacyRepository, times(1)).deleteById(1L);
    }
}
