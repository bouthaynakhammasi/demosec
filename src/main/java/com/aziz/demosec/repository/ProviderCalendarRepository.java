package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ProviderCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderCalendarRepository extends JpaRepository<ProviderCalendar, Long> {
    Optional<ProviderCalendar> findByProvider_Id(Long providerId);
}
