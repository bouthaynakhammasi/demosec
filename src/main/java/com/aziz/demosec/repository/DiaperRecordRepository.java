package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.DiaperRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaperRecordRepository extends JpaRepository<DiaperRecord, Long> {
    List<DiaperRecord> findByBabyProfileIdOrderByChangedAtDesc(Long babyProfileId);
}
