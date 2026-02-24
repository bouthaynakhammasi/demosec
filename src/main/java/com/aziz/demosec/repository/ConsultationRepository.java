package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}