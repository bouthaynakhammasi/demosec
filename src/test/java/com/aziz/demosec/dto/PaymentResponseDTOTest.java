package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.PaymentMethod;
import com.aziz.demosec.Entities.PaymentStatus;
import com.aziz.demosec.dto.pharmacy.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Response DTO Unit Tests")
class PaymentResponseDTOTest {

    private PaymentResponseDTO paymentResponseDTO;

    @BeforeEach
    void setUp() {
        paymentResponseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.STRIPE)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(100.00))
                .currency("TND")
                .transactionId("txn_123")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create payment response DTO with all fields")
    void testCreatePaymentResponseDTO() {
        assertNotNull(paymentResponseDTO);
        assertEquals(1L, paymentResponseDTO.getId());
        assertEquals(1L, paymentResponseDTO.getOrderId());
        assertEquals(PaymentMethod.STRIPE, paymentResponseDTO.getMethod());
        assertEquals(PaymentStatus.PENDING, paymentResponseDTO.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), paymentResponseDTO.getAmount());
        assertEquals("TND", paymentResponseDTO.getCurrency());
        assertEquals("txn_123", paymentResponseDTO.getTransactionId());
    }

    @Test
    @DisplayName("Should set and get client secret")
    void testClientSecret() {
        // When
        paymentResponseDTO.setClientSecret("secret_abc123");

        // Then
        assertEquals("secret_abc123", paymentResponseDTO.getClientSecret());
    }

    @Test
    @DisplayName("Should set and get gateway metadata")
    void testGatewayMetadata() {
        // When
        paymentResponseDTO.setGatewayMetadata("{\"key\": \"value\"}");

        // Then
        assertEquals("{\"key\": \"value\"}", paymentResponseDTO.getGatewayMetadata());
    }

    @Test
    @DisplayName("Should handle null values in optional fields")
    void testNullOptionalFields() {
        // Given
        PaymentResponseDTO dto = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.CASH_ON_DELIVERY)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(50.00))
                .build();

        // Then
        assertNotNull(dto);
        assertNull(dto.getTransactionId());
        assertNull(dto.getClientSecret());
        assertNull(dto.getGatewayMetadata());
    }
}

