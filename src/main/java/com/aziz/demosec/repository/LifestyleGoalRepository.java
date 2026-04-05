package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LifestyleGoal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LifestyleGoalRepository extends JpaRepository<LifestyleGoal, Long> {
    List<LifestyleGoal> findByPatientId(Long patientId);
}
