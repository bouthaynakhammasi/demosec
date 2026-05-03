package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.HealthyRecipe;
import com.aziz.demosec.Entities.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    List<MealPlan> findByLifestylePlanId(Long lifestylePlanId);
    List<MealPlan> findByPatientId(Long patientId);
    List<MealPlan> findByPatientIdAndDayOfWeek(Long patientId, String dayOfWeek);
    List<MealPlan> findByPatientIdAndMealType(Long patientId, String mealType);
    List<MealPlan> findByLifestylePlanIdAndDayOfWeek(Long lifestylePlanId, String dayOfWeek);

    @Query("SELECT mp.recipe FROM MealPlan mp WHERE mp.lifestylePlan.id = :lifestylePlanId")
    List<HealthyRecipe> findRecipesByLifestylePlanId(@Param("lifestylePlanId") Long lifestylePlanId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MealPlan mp WHERE mp.lifestylePlan.id = :lifestylePlanId")
    void deleteByLifestylePlanId(@Param("lifestylePlanId") Long lifestylePlanId);
}
