package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {
    List<ProviderAvailability> findByProvider_Id(Long providerId);
    List<ProviderAvailability> findByProvider_IdAndAvailable(Long providerId, boolean available);
    List<ProviderAvailability> findByProvider_IdAndSpecificDate(Long providerId, LocalDate specificDate);
    List<ProviderAvailability> findByProvider_IdAndDayOfWeekAndSpecificDateIsNull(Long providerId, DayOfWeek dayOfWeek);
    List<ProviderAvailability> findByProvider_IdAndSpecificDateIsNull(Long providerId);
}
