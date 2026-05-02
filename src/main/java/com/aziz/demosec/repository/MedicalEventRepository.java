package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.MedicalEvent;
import com.aziz.demosec.Entities.MedicalEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {
       List<MedicalEvent> findByEventType(MedicalEventType type);
}
