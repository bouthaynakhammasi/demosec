package com.aziz.demosec.payment.gateway.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.aziz.demosec.Entities.Payment;
import com.aziz.demosec.dto.pharmacy.PaymentRequestDTO;
import com.aziz.demosec.payment.gateway.PaymentGatewayProvider;
import com.aziz.demosec.payment.gateway.PaymentGatewayResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Stripe Payment Gateway Implementation
 * Handles real Stripe payment processing
 * 
 * Supports:
 * - Card payments via Stripe.js/Elements
 * - PayPal integration via Stripe
 * - Apple Pay / Google Pay via Stripe
 * - Refunds and disputes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProvider implements PaymentGatewayProvider {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.api.version:2024-04-10}")
    private String stripeApiVersion;

    @Override
    public PaymentGatewayResponse processPayment(Payment payment, PaymentRequestDTO request) {
        try {
            Stripe.apiKey = stripeApiKey;

            // For Stripe, the token should come from client-side Stripe.js
            String paymentMethodId = request.getPaymentToken();

            if (paymentMethodId == null || paymentMethodId.isEmpty()) {
                return PaymentGatewayResponse.failure("INVALID_TOKEN", "Payment method token is required for Stripe");
            }

            // Convert amount to smallest unit
            String currency = payment.getCurrency().toLowerCase();
            long amount;
            if (isThreeDecimalCurrency(currency)) {
                amount = payment.getAmount().multiply(new java.math.BigDecimal(1000)).longValue();
            } else {
                amount = payment.getAmount().multiply(new java.math.BigDecimal(100)).longValue();
            }

            // Create Payment Intent for modern async payments
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .setReturnUrl("http://localhost:4200/payment/confirm") // Updated to frontend URL
                    .setDescription("Order #" + payment.getOrder().getId())
                    .putMetadata("orderId", payment.getOrder().getId().toString())
                    .putMetadata("patientId", payment.getOrder().getPatient().getId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            if ("succeeded".equals(intent.getStatus())) {
                return PaymentGatewayResponse.builder()
                        .success(true)
                        .transactionId(intent.getId())
                        .status("COMPLETED")
                        .amount(payment.getAmount().doubleValue())
                        .currency(payment.getCurrency())
                        .method("STRIPE_CARD")
                        .authorizationCode(intent.getClientSecret())
                        .metadata(Map.of(
                                "latestChargeId", intent.getLatestCharge() != null ? intent.getLatestCharge() : "",
                                "stripeIntentId", intent.getId()))
                        .build();
            } else if ("processing".equals(intent.getStatus())) {
                return PaymentGatewayResponse.processing(intent.getId());
            } else if ("requires_action".equals(intent.getStatus())) {
                // 3D Secure or other challenges
                return PaymentGatewayResponse.builder()
                        .success(false)
                        .transactionId(intent.getId())
                        .status("REQUIRES_AUTH")
                        .errorCode("3D_SECURE_REQUIRED")
                        .errorMessage("3D Secure authentication required")
                        .metadata(Map.of(
                                "clientSecret", intent.getClientSecret(),
                                "nextAction", intent.getNextAction().toString()))
                        .build();
            } else {
                return PaymentGatewayResponse.failure("PAYMENT_FAILED", "Payment intent failed: " + intent.getStatus());
            }

        } catch (StripeException e) {
            log.error("Stripe payment processing error (Code: {}, Msg: {}): ", e.getCode(), e.getMessage(), e);
            return PaymentGatewayResponse.failure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse verifyPayment(String transactionId) {
        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntent intent = PaymentIntent.retrieve(transactionId);

            return PaymentGatewayResponse.builder()
                    .success("succeeded".equals(intent.getStatus()))
                    .transactionId(intent.getId())
                    .status(mapStripeStatus(intent.getStatus()))
                    .metadata(Map.of(
                            "stripeStatus", intent.getStatus(),
                            "amount", intent.getAmount() / (isThreeDecimalCurrency(intent.getCurrency()) ? 1000.0 : 100.0),
                            "currency", intent.getCurrency()))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe payment verification error: ", e);
            return PaymentGatewayResponse.failure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse refundPayment(String transactionId, Double amount) {
        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntent intent = PaymentIntent.retrieve(transactionId);
            String currency = intent.getCurrency();

            RefundCreateParams.Builder params = RefundCreateParams.builder()
                    .setPaymentIntent(transactionId);

            if (amount != null) {
                long refundAmount;
                if (isThreeDecimalCurrency(currency)) {
                    refundAmount = Math.round(amount * 1000);
                } else {
                    refundAmount = Math.round(amount * 100);
                }
                params.setAmount(refundAmount);
            }

            Refund refund = Refund.create(params.build());

            return PaymentGatewayResponse.builder()
                    .success("succeeded".equals(refund.getStatus()))
                    .transactionId(transactionId)
                    .refundId(refund.getId())
                    .status("REFUNDED")
                    .amount(refund.getAmount() / (isThreeDecimalCurrency(currency) ? 1000.0 : 100.0))
                    .metadata(Map.of(
                            "refundStatus", refund.getStatus(),
                            "reason", refund.getReason() != null ? refund.getReason() : "UNMAPPED"))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe refund error: ", e);
            return PaymentGatewayResponse.failure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse cancelPayment(String transactionId) {
        try {
            Stripe.apiKey = stripeApiKey;

            Map<String, Object> params = new HashMap<>();
            params.put("cancellation_reason", "requested_by_customer");

            PaymentIntent intent = PaymentIntent.retrieve(transactionId);
            intent = intent.cancel(params);

            return PaymentGatewayResponse.builder()
                    .success("canceled".equals(intent.getStatus()))
                    .transactionId(intent.getId())
                    .status("CANCELLED")
                    .metadata(Map.of("cancelledAt", intent.getCanceledAt() != null ? intent.getCanceledAt() : 0))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe cancel payment error: ", e);
            return PaymentGatewayResponse.failure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse createIntent(Payment payment) {
        try {
            Stripe.apiKey = stripeApiKey;

            // Convert amount to smallest unit
            String currency = payment.getCurrency().toLowerCase();
            long amount;
            if (isThreeDecimalCurrency(currency)) {
                amount = payment.getAmount().multiply(new java.math.BigDecimal(1000)).longValue();
            } else {
                amount = payment.getAmount().multiply(new java.math.BigDecimal(100)).longValue();
            }

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .setDescription("Order #" + payment.getOrder().getId())
                    .putMetadata("orderId", payment.getOrder().getId().toString())
                    .putMetadata("patientId", payment.getOrder().getPatient().getId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("clientSecret", intent.getClientSecret());
            metadata.put("stripeIntentId", intent.getId());

            return PaymentGatewayResponse.builder()
                    .success(true)
                    .transactionId(intent.getId())
                    .status("PENDING")
                    .amount(payment.getAmount().doubleValue())
                    .currency(payment.getCurrency())
                    .metadata(metadata)
                    .build();

        } catch (StripeException e) {
            log.error("Stripe intent creation failed (Type: {}, Code: {}): {}", e.getClass().getSimpleName(), e.getCode(), e.getMessage());
            return PaymentGatewayResponse.failure(e.getCode(), e.getMessage());
        }
    }

    private boolean isThreeDecimalCurrency(String currency) {
        return "tnd".equalsIgnoreCase(currency) || "jod".equalsIgnoreCase(currency) || 
               "kwd".equalsIgnoreCase(currency) || "omr".equalsIgnoreCase(currency) || 
               "bhd".equalsIgnoreCase(currency) || "iqd".equalsIgnoreCase(currency) || 
               "lyd".equalsIgnoreCase(currency);
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    @Override
    public boolean isConfigured() {
        return stripeApiKey != null && !stripeApiKey.isEmpty() && !stripeApiKey.contains("YOUR_");
    }

    /**
     * Map Stripe payment intent status to application status
     */
    private String mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> "COMPLETED";
            case "processing" -> "PROCESSING";
            case "requires_payment_method", "requires_action" -> "REQUIRES_AUTH";
            case "canceled" -> "CANCELLED";
            default -> "FAILED";
        };
    }
}
