package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.HealthyRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HealthyRecipeRepository extends JpaRepository<HealthyRecipe, Long> {
    List<HealthyRecipe> findByNutritionistId(Long nutritionistId);
    List<HealthyRecipe> findByCategory(String category);

    @Query("""
        SELECT m
        FROM HealthyRecipe m
        JOIN m.allowedDiagnoses ad
        WHERE ad.name = :diagnosis
        AND m.nutrients.calories <= :maxCalories
        AND m.id NOT IN (
            SELECT fd.recipe.id
            FROM FoodDiary fd
            WHERE fd.patient.id = :patientId
            AND fd.recipe.id IS NOT NULL
        )
        ORDER BY m.nutrients.protein DESC
    """)
    List<HealthyRecipe> recommendMeals(String diagnosis, Double maxCalories, Long patientId);
}