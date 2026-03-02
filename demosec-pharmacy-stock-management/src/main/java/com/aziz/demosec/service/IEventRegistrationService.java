package com.aziz.demosec.service;

import com.aziz.demosec.dto.request.EventRegistrationCreateRequest;
import com.aziz.demosec.dto.response.EventRegistrationResponse;

import java.util.List;

public interface IEventRegistrationService {
    EventRegistrationResponse register(EventRegistrationCreateRequest request);
    EventRegistrationResponse validate(Long registrationId);
    List<EventRegistrationResponse> listByEvent(Long eventId);
    List<EventRegistrationResponse> listByParticipant(Long participantId);
}
