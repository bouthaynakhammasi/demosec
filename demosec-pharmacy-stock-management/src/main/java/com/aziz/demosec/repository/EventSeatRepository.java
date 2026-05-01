package com.aziz.demosec.repository;

import com.aziz.demosec.dto.SeatZoneSummaryResponse;
import com.aziz.demosec.entities.EventSeat;
import com.aziz.demosec.entities.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    List<EventSeat> findByEventId(Long eventId);

    @Query("SELECT new com.aziz.demosec.dto.SeatZoneSummaryResponse(" +
            "s.zoneName, COUNT(s.id), " +
            "SUM(CASE WHEN s.status = com.aziz.demosec.entities.SeatStatus.AVAILABLE THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN s.status = com.aziz.demosec.entities.SeatStatus.RESERVED THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN s.status = com.aziz.demosec.entities.SeatStatus.BLOCKED THEN 1L ELSE 0L END)) " +
            "FROM EventSeat s WHERE s.event.id = :eventId GROUP BY s.zoneName")
    List<SeatZoneSummaryResponse> getSeatSummaryByZone(@Param("eventId") Long eventId);
    
    boolean existsByEventIdAndSeatLabel(Long eventId, String seatLabel);
}
