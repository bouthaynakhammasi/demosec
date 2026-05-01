package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsSummaryResponse {
    private Long totalUsers;
    private Long totalEvents;
    private Long totalDonations; // Placeholder for now
    private Long totalPosts; // Placeholder for now
    
    private Map<String, Long> usersByRole;
    private List<MonthlyGrowth> userGrowth;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyGrowth {
        private String month;
        private Long count;
    }
}
