package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.service.ICalendarAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CalendarAvailabilityController {

    private final ICalendarAvailabilityService availabilityService;

    // TODO: Require authentication & verify user is the provider
    @PostMapping("/providers/{providerId}/availabilities")
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarAvailabilityResponse addAvailability(
            @PathVariable("providerId") Long providerId,
            @RequestBody CalendarAvailabilityRequest request) {
        System.out.println("[DEBUG] POST /api/v1/providers/" + providerId + "/availabilities");
        System.out.println("[DEBUG] Request: Start=" + request.getStartTime() + ", End=" + request.getEndTime() + ", Mode=" + request.getMode());
        return availabilityService.createAvailability(providerId, request);
    }

    @GetMapping("/providers/{providerId}/availabilities")
    public List<CalendarAvailabilityResponse> getAvailabilities(
            @PathVariable("providerId") Long providerId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(name = "status", required = false) AvailabilityStatus status) {
        System.out.println("[DEBUG] GET /api/v1/providers/" + providerId + "/availabilities - from=" + from + ", to=" + to);
        return availabilityService.getAvailabilities(providerId, from, to, status);
    }

    @PatchMapping("/availabilities/{availabilityId}")
    public CalendarAvailabilityResponse updateAvailability(
            @PathVariable("availabilityId") Long availabilityId,
            @RequestBody CalendarAvailabilityRequest request) {
        return availabilityService.updateAvailability(availabilityId, request);
    }

    @DeleteMapping("/availabilities/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(@PathVariable("availabilityId") Long availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
    }
}
