package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LaboratoryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
@Repository
public interface LaboratoryStaffRepository extends JpaRepository<LaboratoryStaff, Long> {
=======
import java.util.Optional;

@Repository
public interface LaboratoryStaffRepository extends JpaRepository<LaboratoryStaff, Long> {
    Optional<LaboratoryStaff> findByEmail(String email);
>>>>>>> origin/MedicalRecord
}
