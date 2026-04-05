package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.appointment.Mode;
import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CalendarAvailabilityResponse {
    private Long id;
    private Long providerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private Mode mode;
    private AvailabilityStatus status;
    private String address;
}
