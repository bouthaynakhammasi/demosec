package com.aziz.demosec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aggregated seating statistics for an event.
 * Returned by GET /api/events/seats/{eventId}/stats
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatingStatsResponse {

    private Long   eventId;
    private String eventTitle;
    private String venueType;       // HOTEL | STADIUM | CONFERENCE | null

    // Totals (JPQL aggregate)
    private Long totalSeats;
    private Long availableSeats;
    private Long reservedSeats;
    private Long blockedSeats;

    // Per-zone breakdown
    private List<SeatZoneSummaryResponse> byZone;
}
