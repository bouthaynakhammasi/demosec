package com.aziz.demosec.controller;

import com.aziz.demosec.dto.HealthyRecipeDTO;
import com.aziz.demosec.service.HealthyRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/healthy-recipes")
@RequiredArgsConstructor
public class HealthyRecipeController {

    private final HealthyRecipeService recipeService;

    @PostMapping
    public ResponseEntity<HealthyRecipeDTO> create(@RequestBody HealthyRecipeDTO dto) {
        return ResponseEntity.ok(recipeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<HealthyRecipeDTO>> getAll() {
        return ResponseEntity.ok(recipeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthyRecipeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<HealthyRecipeDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(recipeService.getByCategory(category));
    }

    @GetMapping("/nutritionist/{nutritionistId}")
    public ResponseEntity<List<HealthyRecipeDTO>> getByNutritionist(@PathVariable Long nutritionistId) {
        return ResponseEntity.ok(recipeService.getByNutritionist(nutritionistId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthyRecipeDTO> update(@PathVariable Long id, @RequestBody HealthyRecipeDTO dto) {
        return ResponseEntity.ok(recipeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
