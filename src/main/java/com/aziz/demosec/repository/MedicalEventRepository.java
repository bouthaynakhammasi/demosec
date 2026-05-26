package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.MedicalEvent;
import com.aziz.demosec.Entities.MedicalEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {
    List<MedicalEvent> findByEventType(MedicalEventType type);
    List<MedicalEvent> findByDateAfterOrderByDateAsc(LocalDateTime date);
}