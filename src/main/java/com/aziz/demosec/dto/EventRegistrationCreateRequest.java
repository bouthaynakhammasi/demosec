package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRegistrationCreateRequest {

    @NotNull(message = "Medical event ID is required")
    @Positive(message = "Medical event ID must be positive")
    private Long eventId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long participantId;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    private boolean attended;
}

