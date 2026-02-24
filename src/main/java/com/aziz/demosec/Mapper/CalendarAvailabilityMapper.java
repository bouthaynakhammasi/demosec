package com.aziz.demosec.Mapper;


import com.aziz.demosec.Entities.CalendarAvailability;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import org.springframework.stereotype.Component;

@Component
public class CalendarAvailabilityMapper {

    public CalendarAvailabilityResponse toDto(CalendarAvailability availability) {
        if (availability == null) return null;

        return CalendarAvailabilityResponse.builder()
                .id(availability.getId())
                .calendarId(
                        availability.getCalendar() != null ? availability.getCalendar().getId() : null
                )
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .mode(availability.getMode())
                .status(availability.getStatus())
                .address(availability.getAddress())
                .build();
    }
}
