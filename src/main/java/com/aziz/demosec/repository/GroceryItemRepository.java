package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {

    List<GroceryItem> findByPatientId(Long patientId);
    List<GroceryItem> findByLifestylePlanId(Long lifestylePlanId);
    List<GroceryItem> findByPatientIdAndPurchased(Long patientId, boolean purchased);
    List<GroceryItem> findByLifestylePlanIdAndPurchased(Long lifestylePlanId, boolean purchased);
    void deleteByLifestylePlanId(Long lifestylePlanId);
}