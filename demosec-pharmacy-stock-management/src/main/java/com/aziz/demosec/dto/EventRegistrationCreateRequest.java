package com.aziz.demosec.dto.request;

import lombok.Data;

@Data
public class EventRegistrationCreateRequest {
    private Long eventId;
    private Long participantId;
}