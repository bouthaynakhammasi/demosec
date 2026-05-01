package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatZoneSummaryResponse {
    private String zoneName;
    private Long totalSeats;
    private Long availableSeats;
    private Long reservedSeats;
    private Long blockedSeats;
}
