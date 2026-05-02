package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalyticsDTO {
    private Long eventId;
    private String eventTitle;
    private long totalRegistrations;
    private long actualAttendance;
    private double attendanceRate;
    private double averageSatisfaction;
    private double totalRevenue;
    private double attendanceDrift;
    private double satisfactionDrift;
    private List<String> recommendations;
}
