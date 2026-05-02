package com.aziz.demosec.controller;

import com.aziz.demosec.dto.GroceryItemDTO;
import com.aziz.demosec.service.GroceryListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-lists")
@RequiredArgsConstructor
public class GroceryListController {

    private final GroceryListService groceryService;

    @PostMapping("/generate")
    public ResponseEntity<List<GroceryItemDTO>> generate(
            @RequestParam Long lifestylePlanId, @RequestParam Long patientId) {
        return ResponseEntity.ok(groceryService.generateFromLifestylePlan(lifestylePlanId, patientId));
    }

    @GetMapping("/lifestyle-plan/{lifestylePlanId}")
    public ResponseEntity<List<GroceryItemDTO>> getByLifestylePlan(@PathVariable Long lifestylePlanId) {
        return ResponseEntity.ok(groceryService.getByLifestylePlan(lifestylePlanId));
    }

    @GetMapping("/lifestyle-plan/{lifestylePlanId}/pending")
    public ResponseEntity<List<GroceryItemDTO>> getPendingItems(@PathVariable Long lifestylePlanId) {
        return ResponseEntity.ok(groceryService.getPendingItems(lifestylePlanId));
    }

    @PutMapping("/{itemId}/purchase")
    public ResponseEntity<GroceryItemDTO> markPurchased(@PathVariable Long itemId) {
        return ResponseEntity.ok(groceryService.markPurchased(itemId));
    }

    @DeleteMapping("/lifestyle-plan/{lifestylePlanId}")
    public ResponseEntity<Void> clearList(@PathVariable Long lifestylePlanId) {
        groceryService.clearList(lifestylePlanId);
        return ResponseEntity.noContent().build();
    }
}
