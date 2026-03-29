package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EventRegistrationCreateRequest {

    @NotNull(message = "Event ID is required")
    @Positive(message = "Event ID must be positive")
    private Long eventId;

    @NotNull(message = "Participant ID is required")
    @Positive(message = "Participant ID must be positive")
    private Long participantId;
}