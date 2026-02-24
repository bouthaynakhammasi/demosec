package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}