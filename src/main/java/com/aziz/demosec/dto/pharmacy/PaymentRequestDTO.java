package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod method;

    // For Stripe/D17 – contains the token/reference returned by the frontend SDK
    private String paymentToken;
}
