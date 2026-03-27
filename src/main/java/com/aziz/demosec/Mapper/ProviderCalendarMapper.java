package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.dto.ProviderCalendarResponse;
import org.springframework.stereotype.Component;

@Component
public class ProviderCalendarMapper {

    public ProviderCalendarResponse toDto(ProviderCalendar calendar) {
        if (calendar == null) return null;

        return ProviderCalendarResponse.builder()
                .id(calendar.getId())
                .providerId(
                        calendar.getProvider() != null ? calendar.getProvider().getId() : null
                )
                .build();
    }
}
