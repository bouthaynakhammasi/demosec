package com.aziz.demosec.delivery;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private String trackingId;
    private String trackingUrl;
    private LocalDateTime estimatedArrival;
    private String agencyName;
}
