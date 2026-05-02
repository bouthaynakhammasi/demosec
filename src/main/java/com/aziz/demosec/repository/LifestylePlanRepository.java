package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.LifestylePlan;

import java.util.List;

import com.aziz.demosec.Entities.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LifestylePlanRepository extends JpaRepository<LifestylePlan, Long> {
    List<LifestylePlan> findByGoalId(Long goalId);
    List<LifestylePlan> findByGoal_Patient_Id(Long patientId);
    // Find active plans for a patient
    List<LifestylePlan> findByGoal_Patient_IdAndStatus(Long patientId, PlanStatus status);

    // Sum expected calories from all MealPlans linked to this LifestylePlan
    @Query("SELECT SUM(mp.recipe.nutrients.calories) FROM MealPlan mp WHERE mp.lifestylePlan.id = :planId")
    Double sumExpectedCaloriesByPlanId(Long planId);
}