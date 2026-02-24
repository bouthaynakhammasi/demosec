package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.AvailabilityMode;
import com.aziz.demosec.Entities.AvailabilityStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CalendarAvailabilityRequest {

    private Long calendarId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private AvailabilityMode mode;
    private AvailabilityStatus status;

    private String address;
}