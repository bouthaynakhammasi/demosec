package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
}