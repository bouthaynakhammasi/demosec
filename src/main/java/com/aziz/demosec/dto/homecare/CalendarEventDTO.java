package com.aziz.demosec.dto.homecare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String title;
    private String type; // AVAILABLE, BUSY, REQUEST, BLOCKED
    private String status; // For REQUEST type (ACCEPTED, IN_PROGRESS, etc.)
    private String patientName;
    private Long requestId;
}
