package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.appointment.AvailabilityStatus;
import com.aziz.demosec.Entities.appointment.CalendarAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarAvailabilityRepository extends JpaRepository<CalendarAvailability, Long> {

    @Query("SELECT COUNT(a) FROM CalendarAvailability a " +
            "WHERE a.calendar.id = :calendarId " +
            "AND a.startTime < :endTime " +
            "AND a.endTime > :startTime " +
            "AND (:excludeId IS NULL OR a.id <> :excludeId)")
    long countOverlappingAvailabilities(
            @Param("calendarId") Long calendarId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId
    );

    List<CalendarAvailability> findByCalendar_Provider_IdAndStatusAndStartTimeBetween(
            Long providerId, AvailabilityStatus status, LocalDateTime start, LocalDateTime end
    );

    List<CalendarAvailability> findByCalendar_Provider_IdAndStartTimeBetween(
            Long providerId, LocalDateTime start, LocalDateTime end
    );

    List<CalendarAvailability> findByCalendar_Provider_Id(Long providerId);

    List<CalendarAvailability> findByCalendar_Provider_IdAndStartTimeBetweenAndStatusNot(
            Long providerId, LocalDateTime startTime, LocalDateTime endTime, AvailabilityStatus status);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByCalendar_IdAndStartTimeAfterAndStatus(Long calendarId, LocalDateTime startTime, AvailabilityStatus status);
}
