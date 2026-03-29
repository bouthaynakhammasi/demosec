package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.PharmacyOrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrackingResponseDTO {

    private Long id;
    private Long orderId;
    private PharmacyOrderStatus status;
    private String note;
    private String changedBy;
    private LocalDateTime changedAt;
}
