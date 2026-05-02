package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LifestylePlan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LifestylePlanRepository extends JpaRepository<LifestylePlan, Long> {
    List<LifestylePlan> findByGoalId(Long goalId);
    List<LifestylePlan> findByGoal_Patient_Id(Long patientId);
}