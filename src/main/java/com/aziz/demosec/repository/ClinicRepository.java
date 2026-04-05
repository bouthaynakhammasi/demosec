package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
=======
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findByEmail(String email);
>>>>>>> origin/MedicalRecord
}
