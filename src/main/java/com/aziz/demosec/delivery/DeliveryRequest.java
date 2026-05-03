package com.aziz.demosec.delivery;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequest {
    private String pickupAddress;
    private String dropoffAddress;
    private String packageDescription;
    private String externalOrderRef; // our order ID as string
    // GPS coordinates for Haversine ETA calculation (optional)
    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;
}
