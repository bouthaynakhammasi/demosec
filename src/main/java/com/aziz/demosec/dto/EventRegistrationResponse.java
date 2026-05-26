package com.aziz.demosec.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRegistrationResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long participantId;
    private String participantName;
    private String notes;
    private boolean attended;
    private LocalDateTime registrationDate;
}

