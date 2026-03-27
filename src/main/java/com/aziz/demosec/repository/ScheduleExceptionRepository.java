package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {
    List<ScheduleException> findByProviderId(Long providerId);
    
    // Find exceptions that cover a specific date
    List<ScheduleException> findByProviderIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long providerId, LocalDate date, LocalDate sameDate);
}
