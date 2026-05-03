package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventAnalyticsResponse {
    private Long eventId;
    private String eventTitle;
    private Long totalRegistrations;
    private Long actualAttendance;
    private Double attendanceRate; // percentage
    private Double averageSatisfaction; // 1-5
    private Double totalRevenue;
    
    // Comparison metrics
    private Double attendanceDrift; // +/- vs previous event
    private Double satisfactionDrift;
    
    private List<String> feedbackKeywords;
    private List<String> recommendations;
}
