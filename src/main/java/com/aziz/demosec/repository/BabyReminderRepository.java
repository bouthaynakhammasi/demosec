package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.BabyReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BabyReminderRepository extends JpaRepository<BabyReminder, Long> {
    List<BabyReminder> findByBabyProfileIdOrderByDueDate(Long babyProfileId);
}
