package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.CalendarAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarAvailabilityRepository extends JpaRepository<CalendarAvailability, Long> {
}