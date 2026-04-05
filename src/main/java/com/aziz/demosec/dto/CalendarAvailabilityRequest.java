package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.appointment.Mode;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CalendarAvailabilityRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Mode mode;
    private String address;
}
