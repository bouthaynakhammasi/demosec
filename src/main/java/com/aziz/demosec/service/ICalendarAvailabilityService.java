package com.aziz.demosec.service;

import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ICalendarAvailabilityService {
    ProviderCalendar getOrCreateCalendar(Long providerId);
    CalendarAvailabilityResponse createAvailability(Long providerId, CalendarAvailabilityRequest request);
    CalendarAvailabilityResponse updateAvailability(Long availabilityId, CalendarAvailabilityRequest request);
    void deleteAvailability(Long availabilityId);
    
    // Additional methods for controllers
    List<CalendarAvailabilityResponse> getAvailabilities(Long providerId, LocalDateTime from, LocalDateTime to, AvailabilityStatus status);
}
