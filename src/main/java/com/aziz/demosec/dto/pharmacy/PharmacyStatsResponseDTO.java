package com.aziz.demosec.dto.pharmacy;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyStatsResponseDTO {
    private Long pharmacyId;
    private String pharmacyName;
    private long totalOrders;
    private long pendingOrders;
    private long validatedOrders;
    private long rejectedOrders;
    private long cancelledOrders;
    private long deliveredOrders;
    private BigDecimal totalRevenue;
    private List<TopProductDTO> topProducts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private long totalQuantitySold;
        private BigDecimal totalRevenue;
    }
}
