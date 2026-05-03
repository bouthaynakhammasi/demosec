package com.aziz.demosec.dto.homecare;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AvailabilityDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private LocalDate specificDate;  // null = règle répétée
}
