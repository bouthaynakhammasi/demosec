package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByBabyProfileIdOrderByCreatedAtDesc(Long babyProfileId);
}
