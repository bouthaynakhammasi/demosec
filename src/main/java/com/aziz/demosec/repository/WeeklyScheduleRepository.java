package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {
    Optional<WeeklySchedule> findByProvider_Id(Long providerId);
}
