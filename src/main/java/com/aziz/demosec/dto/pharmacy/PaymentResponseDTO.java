package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.PaymentMethod;
import com.aziz.demosec.Entities.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private String transactionId;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime confirmedAt;
    private String gatewayMetadata;
    private String clientSecret;
}
