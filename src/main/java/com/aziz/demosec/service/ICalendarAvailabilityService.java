package com.aziz.demosec.service;

import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;

import java.util.List;

public interface ICalendarAvailabilityService {

    CalendarAvailabilityResponse addAvailability(CalendarAvailabilityRequest request);

    CalendarAvailabilityResponse selectAvailabilityByIdWithGet(Long id);
    CalendarAvailabilityResponse selectAvailabilityByIdWithOrElse(Long id);

    List<CalendarAvailabilityResponse> selectAllAvailabilities();

    CalendarAvailabilityResponse updateAvailability(Long id, CalendarAvailabilityRequest request);

    void deleteAvailabilityById(Long id);
    void deleteAllAvailabilities();

    long countingAvailabilities();
    boolean verifAvailabilityById(Long id);
}