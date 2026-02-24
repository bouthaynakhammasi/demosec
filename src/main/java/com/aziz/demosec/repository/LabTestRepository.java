package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByLaboratoryId(Long laboratoryId);
    boolean existsByNameAndLaboratoryId(String name, Long laboratoryId);
}