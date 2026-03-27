package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.appointment.CalendarAvailability;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import org.springframework.stereotype.Component;

@Component
public class CalendarAvailabilityMapper {

    public CalendarAvailabilityResponse toDto(CalendarAvailability availability) {
        if (availability == null) return null;

        Long providerId = null;
        if (availability.getCalendar() != null && availability.getCalendar().getProvider() != null) {
            providerId = availability.getCalendar().getProvider().getId();
        }

        return CalendarAvailabilityResponse.builder()
                .id(availability.getId())
                .providerId(providerId)
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .mode(availability.getMode())
                .status(availability.getStatus())
                .address(availability.getAddress())
                .build();
    }
}
