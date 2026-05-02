package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Nutrients;
import com.aziz.demosec.dto.HealthyRecipeDTO;
import com.aziz.demosec.Entities.HealthyRecipe;
import com.aziz.demosec.Entities.Nutritionist;
import com.aziz.demosec.repository.HealthyRecipeRepository;
import com.aziz.demosec.repository.NutritionistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthyRecipeService {

    private final HealthyRecipeRepository recipeRepository;
    private final NutritionistRepository nutritionistRepository;

    // Nutritionist creates a recipe
    public HealthyRecipeDTO create(HealthyRecipeDTO dto) {
        Nutritionist nutritionist = nutritionistRepository.findById(dto.getNutritionistId())
                .orElseThrow(() -> new RuntimeException("Nutritionist not found"));

        HealthyRecipe recipe = HealthyRecipe.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .ingredients(dto.getIngredients())
                .instructions(dto.getInstructions())
                .nutrients(Nutrients.builder()
                        .calories(dto.getCalories() != null ? dto.getCalories().doubleValue() : 0.0)
                        .protein(0.0) // Defaults for now
                        .carbs(0.0)
                        .fats(0.0)
                        .build())
                .prepTimeMinutes(dto.getPrepTimeMinutes())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .nutritionist(nutritionist)
                .build();

        return toDTO(recipeRepository.save(recipe));
    }

    // Patients view all recipes
    public List<HealthyRecipeDTO> getAll() {
        return recipeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Patients view by category
    public List<HealthyRecipeDTO> getByCategory(String category) {
        return recipeRepository.findByCategory(category).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Nutritionist views their own recipes
    public List<HealthyRecipeDTO> getByNutritionist(Long nutritionistId) {
        return recipeRepository.findByNutritionistId(nutritionistId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Get single recipe
    public HealthyRecipeDTO getById(Long id) {
        return toDTO(recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found")));
    }

    // Nutritionist deletes their recipe
    public void delete(Long id) {
        recipeRepository.deleteById(id);
    }

    public HealthyRecipeDTO update(Long id, HealthyRecipeDTO dto) {
        HealthyRecipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (dto.getNutritionistId() != null) {
            Nutritionist nutritionist = nutritionistRepository.findById(dto.getNutritionistId())
                    .orElseThrow(() -> new RuntimeException("Nutritionist not found"));
            recipe.setNutritionist(nutritionist);
        }

        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setIngredients(dto.getIngredients());
        recipe.setInstructions(dto.getInstructions());
        
        if (recipe.getNutrients() == null) {
            recipe.setNutrients(new Nutrients());
        }
        recipe.getNutrients().setCalories(dto.getCalories() != null ? dto.getCalories().doubleValue() : 0.0);
        
        recipe.setPrepTimeMinutes(dto.getPrepTimeMinutes());
        recipe.setCategory(dto.getCategory());
        recipe.setImageUrl(dto.getImageUrl());

        return toDTO(recipeRepository.save(recipe));
    }

    private HealthyRecipeDTO toDTO(HealthyRecipe r) {
        return HealthyRecipeDTO.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .ingredients(r.getIngredients())
                .instructions(r.getInstructions())
                .calories(r.getNutrients() != null ? r.getNutrients().getCalories().intValue() : 0)
                .prepTimeMinutes(r.getPrepTimeMinutes())
                .category(r.getCategory())
                .imageUrl(r.getImageUrl())
                .nutritionistId(r.getNutritionist() != null ? r.getNutritionist().getId() : null)
                .nutritionistName(r.getNutritionist() != null ? r.getNutritionist().getFullName() : "Unknown") 
                .build();
    }
}
