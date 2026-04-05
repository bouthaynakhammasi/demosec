package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ProgressTracking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressTrackingRepository extends JpaRepository<ProgressTracking, Long> {
    List<ProgressTracking> findByGoalId(Long goalId);
    List<ProgressTracking> findByPatientId(Long patientId);
}