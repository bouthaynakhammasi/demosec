package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.FoodDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FoodDiaryRepository extends JpaRepository<FoodDiary, Long> {

    List<FoodDiary> findByPatientIdAndDate(Long patientId, LocalDate date);
    List<FoodDiary> findByPatientIdAndDateBetween(Long patientId, LocalDate from, LocalDate to);
    List<FoodDiary> findByLifestylePlanId(Long lifestylePlanId);

    @Query("SELECT SUM(f.calories) FROM FoodDiary f WHERE f.patient.id = :patientId AND f.date = :date")
    Integer sumCaloriesByPatientAndDate(Long patientId, LocalDate date);
}