package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LaboratoryStaff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryStaffRepository extends JpaRepository<LaboratoryStaff, Long> {
}