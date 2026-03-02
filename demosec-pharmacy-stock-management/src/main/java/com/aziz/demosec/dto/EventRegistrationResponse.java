package com.aziz.demosec.dto.response;

import com.aziz.demosec.entities.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRegistrationResponse {
    private Long id;
    private Long eventId;
    private Long participantId;
    private RegistrationStatus status;
    private LocalDateTime createdAt;
}