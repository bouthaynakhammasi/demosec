package com.aziz.demosec.payment.gateway;

import com.aziz.demosec.Entities.Payment;
import com.aziz.demosec.dto.pharmacy.PaymentRequestDTO;

/**
 * Payment Gateway Provider Interface
 * Defines contract for payment processing implementations
 */
public interface PaymentGatewayProvider {

    /**
     * Initialize payment with third-party gateway
     * 
     * @param payment Payment entity
     * @param request Request details including token/method
     * @return Gateway response with transaction ID
     */
    PaymentGatewayResponse processPayment(Payment payment, PaymentRequestDTO request);

    /**
     * Verify payment status with gateway
     * 
     * @param transactionId Gateway transaction ID
     * @return Gateway response with current status
     */
    PaymentGatewayResponse verifyPayment(String transactionId);

    /**
     * Refund a payment
     * 
     * @param transactionId Original transaction ID
     * @param amount        Amount to refund (null = full refund)
     * @return Refund response with refund ID
     */
    PaymentGatewayResponse refundPayment(String transactionId, Double amount);

    /**
     * Cancel a payment (before it completes)
     * 
     * @param transactionId Transaction ID to cancel
     * @return Response indicating success/failure
     */
    PaymentGatewayResponse cancelPayment(String transactionId);

    /**
     * Get gateway name
     */
    String getProviderName();

    /**
     * Check if gateway is available/configured
     */
    boolean isConfigured();

    /**
     * Create a payment intent (before confirmation)
     * Useful for modern Stripe Elements flow
     */
    default PaymentGatewayResponse createIntent(Payment payment) {
        return PaymentGatewayResponse.failure("NOT_IMPLEMENTED", "This provider does not support intent creation");
    }
}
