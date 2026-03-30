package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);
    
}
