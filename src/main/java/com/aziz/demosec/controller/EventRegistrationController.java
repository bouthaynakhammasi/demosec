package com.aziz.demosec.controller;

import com.aziz.demosec.dto.EventRegistrationCreateRequest;
import com.aziz.demosec.dto.EventRegistrationResponse;
import com.aziz.demosec.service.IEventRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final IEventRegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRegistrationResponse register(@Valid @RequestBody EventRegistrationCreateRequest request) {
        return registrationService.register(request);
    }

    @PatchMapping("/{id}/validate")
    public EventRegistrationResponse validate(@PathVariable Long id) {
        return registrationService.validate(id);
    }

    @GetMapping("/event/{eventId}")
    public List<EventRegistrationResponse> byEvent(@PathVariable Long eventId) {
        return registrationService.listByEvent(eventId);
    }

    @GetMapping("/participant/{participantId}")
    public List<EventRegistrationResponse> byParticipant(@PathVariable Long participantId) {
        return registrationService.listByParticipant(participantId);
    }
}