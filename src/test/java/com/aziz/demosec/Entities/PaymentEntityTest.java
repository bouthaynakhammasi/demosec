package com.aziz.demosec.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Entity Unit Tests")
class PaymentEntityTest {

    private Payment payment;
    private PharmacyOrder order;

    @BeforeEach
    void setUp() {
        order = PharmacyOrder.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(100.00))
                .status(PharmacyOrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        payment = Payment.builder()
                .id(1L)
                .order(order)
                .method(PaymentMethod.STRIPE)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(100.00))
                .currency("TND")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create payment with all required fields")
    void testCreatePaymentWithRequiredFields() {
        assertNotNull(payment);
        assertEquals(1L, payment.getId());
        assertEquals(PaymentMethod.STRIPE, payment.getMethod());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), payment.getAmount());
        assertEquals("TND", payment.getCurrency());
    }

    @Test
    @DisplayName("Should update payment status")
    void testUpdatePaymentStatus() {
        // Given
        payment.setStatus(PaymentStatus.COMPLETED);

        // Then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    @DisplayName("Should set transaction ID")
    void testSetTransactionId() {
        // Given
        payment.setTransactionId("stripe_txn_123");

        // Then
        assertEquals("stripe_txn_123", payment.getTransactionId());
    }

    @Test
    @DisplayName("Should set and get confirmed date")
    void testConfirmedAt() {
        // Given
        LocalDateTime confirmDate = LocalDateTime.now();
        payment.setConfirmedAt(confirmDate);

        // Then
        assertEquals(confirmDate, payment.getConfirmedAt());
    }

    @Test
    @DisplayName("Should set and get gateway metadata")
    void testGatewayMetadata() {
        // Given
        String metadata = "{\"key\": \"value\"}";
        payment.setGatewayMetadata(metadata);

        // Then
        assertEquals(metadata, payment.getGatewayMetadata());
    }

    @Test
    @DisplayName("Should verify payment belongs to order")
    void testPaymentBelongsToOrder() {
        assertNotNull(payment.getOrder());
        assertEquals(order.getId(), payment.getOrder().getId());
    }

    @Test
    @DisplayName("Should handle all payment methods")
    void testAllPaymentMethods() {
        // Test different payment methods
        Payment cashPayment = Payment.builder()
                .method(PaymentMethod.CASH_ON_DELIVERY)
                .build();
        assertEquals(PaymentMethod.CASH_ON_DELIVERY, cashPayment.getMethod());

        Payment stripePayment = Payment.builder()
                .method(PaymentMethod.STRIPE)
                .build();
        assertEquals(PaymentMethod.STRIPE, stripePayment.getMethod());
    }

    @Test
    @DisplayName("Should handle all payment statuses")
    void testAllPaymentStatuses() {
        // Test pending
        Payment pending = Payment.builder().status(PaymentStatus.PENDING).build();
        assertEquals(PaymentStatus.PENDING, pending.getStatus());

        // Test completed
        Payment completed = Payment.builder().status(PaymentStatus.COMPLETED).build();
        assertEquals(PaymentStatus.COMPLETED, completed.getStatus());

        // Test failed
        Payment failed = Payment.builder().status(PaymentStatus.FAILED).build();
        assertEquals(PaymentStatus.FAILED, failed.getStatus());

        // Test refunded
        Payment refunded = Payment.builder().status(PaymentStatus.REFUNDED).build();
        assertEquals(PaymentStatus.REFUNDED, refunded.getStatus());
    }
}

