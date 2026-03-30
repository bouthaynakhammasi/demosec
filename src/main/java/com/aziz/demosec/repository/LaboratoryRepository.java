package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
}
