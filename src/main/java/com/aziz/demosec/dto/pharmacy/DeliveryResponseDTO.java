package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponseDTO {

    private Long id;
    private Long orderId;
    private String agencyName;
    private String externalTrackingId;
    private String trackingUrl;
    private DeliveryStatus status;
    private Double currentLat;
    private Double currentLng;
    private LocalDateTime estimatedArrival;
    private LocalDateTime requestedAt;
    private LocalDateTime deliveredAt;
}
