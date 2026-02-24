package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}