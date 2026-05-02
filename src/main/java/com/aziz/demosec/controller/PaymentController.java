package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.PaymentRequestDTO;
import com.aziz.demosec.dto.pharmacy.PaymentResponseDTO;
import com.aziz.demosec.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pharmacy/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiatePayment(dto));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getByOrderId(@PathVariable("orderId") Long orderId) {
        try {
            return ResponseEntity.ok(paymentService.getByOrderId(orderId));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            // No payment for this order yet - return null (patient hasn't paid)
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(orderId));
    }

    @PostMapping("/verify/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> verifyPayment(@PathVariable("paymentId") Long paymentId) {
        return ResponseEntity.ok(paymentService.verifyPaymentWithGateway(paymentId));
    }
}
