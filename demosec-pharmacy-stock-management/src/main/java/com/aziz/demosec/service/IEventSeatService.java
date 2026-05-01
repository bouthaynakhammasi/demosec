package com.aziz.demosec.service;

import com.aziz.demosec.dto.SaveSeatRequest;
import com.aziz.demosec.dto.SeatResponse;
import com.aziz.demosec.dto.SeatZoneSummaryResponse;

import java.util.List;

public interface IEventSeatService {
    List<SeatResponse> getEventSeats(Long eventId);
    List<SeatZoneSummaryResponse> getEventSeatSummary(Long eventId);
    void saveSeatsBatch(Long eventId, List<SaveSeatRequest> requests);
    void reserveSeat(Long seatId, String userEmail);
    void releaseSeat(Long seatId);
}
