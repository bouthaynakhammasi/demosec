package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
}