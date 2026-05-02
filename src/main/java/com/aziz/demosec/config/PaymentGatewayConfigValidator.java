package com.aziz.demosec.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.aziz.demosec.payment.gateway.PaymentGatewayFactory;

/**
 * Payment Gateway Configuration Validator
 * Validates payment gateway configuration on application startup
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGatewayConfigValidator {

    private final PaymentGatewayFactory paymentGatewayFactory;

    @EventListener(ApplicationReadyEvent.class)
    public void validatePaymentGatewayConfig() {
        log.info("=".repeat(60));
        log.info("PAYMENT GATEWAY CONFIGURATION VALIDATION");
        log.info("=".repeat(60));

        paymentGatewayFactory.validateConfiguration();

        log.info("=".repeat(60));
        log.info("To configure payment gateways:");
        log.info("1. Stripe: Set STRIPE_API_KEY, STRIPE_PUBLIC_KEY, STRIPE_WEBHOOK_SECRET");
        log.info("2. D17:    Set D17_API_KEY, D17_MERCHANT_ID, D17_WEBHOOK_SECRET");
        log.info("3. Webhooks: Configure in Stripe/D17 dashboards:");
        log.info("   - Stripe: /api/pharmacy/payments/webhook/stripe");
        log.info("   - D17:    /api/pharmacy/payments/webhook/d17");
        log.info("=".repeat(60));
    }
}
