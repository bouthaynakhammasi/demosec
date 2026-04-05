package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
=======
import java.util.Optional;

@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
    Optional<Pharmacist> findByEmail(String email);
>>>>>>> origin/MedicalRecord
}
