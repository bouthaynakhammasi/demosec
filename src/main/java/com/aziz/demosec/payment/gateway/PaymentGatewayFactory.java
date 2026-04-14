package com.aziz.demosec.payment.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.aziz.demosec.Entities.PaymentMethod;
import com.aziz.demosec.payment.gateway.impl.D17PaymentProvider;
import com.aziz.demosec.payment.gateway.impl.StripePaymentProvider;

/**
 * Payment Gateway Factory
 * Routes payment requests to appropriate gateway provider
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final StripePaymentProvider stripePaymentProvider;
    private final D17PaymentProvider d17PaymentProvider;

    /**
     * Get the appropriate payment gateway provider based on payment method
     */
    public PaymentGatewayProvider getProvider(PaymentMethod method) {
        return switch (method) {
            case STRIPE -> {
                if (!stripePaymentProvider.isConfigured()) {
                    log.warn("Stripe provider not configured, using mock mode");
                }
                yield stripePaymentProvider;
            }
            case D17 -> {
                if (!d17PaymentProvider.isConfigured()) {
                    log.warn("D17 provider not configured, using mock mode");
                }
                yield d17PaymentProvider;
            }
            case CASH_ON_DELIVERY -> null; // No gateway needed for COD
            case BANK_CARD -> stripePaymentProvider; // Use Stripe for bank cards
            default -> {
                log.warn("Unknown payment method: {}, defaulting to Stripe", method);
                yield stripePaymentProvider;
            }
        };
    }

    /**
     * Get provider by name
     */
    public PaymentGatewayProvider getProviderByName(String providerName) {
        return switch (providerName.toUpperCase()) {
            case "STRIPE" -> stripePaymentProvider;
            case "D17" -> d17PaymentProvider;
            default -> {
                log.warn("Unknown provider name: {}", providerName);
                yield null;
            }
        };
    }

    /**
     * Get all available providers
     */
    public PaymentGatewayProvider[] getAllProviders() {
        return new PaymentGatewayProvider[] {
                stripePaymentProvider,
                d17PaymentProvider
        };
    }

    /**
     * Check if all required providers are configured
     */
    public void validateConfiguration() {
        log.info("=== Payment Gateways Configuration Status ===");
        log.info("Stripe: {}", stripePaymentProvider.isConfigured() ? "✓ CONFIGURED" : "✗ NOT CONFIGURED");
        log.info("D17: {}", d17PaymentProvider.isConfigured() ? "✓ CONFIGURED" : "✗ NOT CONFIGURED");
        log.info("============================================");
    }
}
