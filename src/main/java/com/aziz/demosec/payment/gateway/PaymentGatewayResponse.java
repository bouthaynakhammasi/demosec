package com.aziz.demosec.payment.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Gateway Response DTO
 * Standardized response from payment gateways
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayResponse {

    private Boolean success;
    private String transactionId;
    private String refundId;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    private String errorCode;
    private String errorMessage;
    private Double amount;
    private String currency;
    private String method; // CARD, PAYPAL, BANK_TRANSFER, etc.
    private String authorizationCode;
    private String cardLast4; // Last 4 digits for card payments
    private String cardBrand; // VISA, MASTERCARD, etc.
    private LocalDateTime processedAt;
    private Map<String, Object> metadata; // Gateway-specific data

    public static PaymentGatewayResponse success(String transactionId,
            Double amount, String currency, String method) {
        return PaymentGatewayResponse.builder()
                .success(true)
                .transactionId(transactionId)
                .status("COMPLETED")
                .amount(amount)
                .currency(currency)
                .method(method)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentGatewayResponse failure(String errorCode, String errorMessage) {
        return PaymentGatewayResponse.builder()
                .success(false)
                .status("FAILED")
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentGatewayResponse processing(String transactionId) {
        return PaymentGatewayResponse.builder()
                .success(true)
                .transactionId(transactionId)
                .status("PROCESSING")
                .processedAt(LocalDateTime.now())
                .build();
    }
}
