package com.aziz.demosec.service;

import com.aziz.demosec.dto.FoodDiaryDTO;
import com.aziz.demosec.Entities.FoodDiary;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.LifestylePlan;
import com.aziz.demosec.repository.FoodDiaryRepository;
import com.aziz.demosec.repository.PatientRepository;
import com.aziz.demosec.repository.LifestylePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodDiaryService {

    private final FoodDiaryRepository foodDiaryRepository;
    private final PatientRepository patientRepository;
    private final LifestylePlanRepository lifestylePlanRepository;

    public FoodDiaryDTO create(FoodDiaryDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LifestylePlan lifestylePlan = null;
        if (dto.getLifestylePlanId() != null) {
            lifestylePlan = lifestylePlanRepository.findById(dto.getLifestylePlanId())
                    .orElseThrow(() -> new RuntimeException("LifestylePlan not found"));
        }

        FoodDiary diary = FoodDiary.builder()
                .patient(patient)
                .lifestylePlan(lifestylePlan)
                .date(dto.getDate())
                .mealType(dto.getMealType())
                .foodName(dto.getFoodName())
                .calories(dto.getCalories())
                .notes(dto.getNotes())
                .imageUrl(dto.getImageUrl())
                .build();

        return toDTO(foodDiaryRepository.save(diary));
    }

    public List<FoodDiaryDTO> getByPatient(Long patientId) {
        return foodDiaryRepository.findByPatientIdAndDateBetween(patientId, LocalDate.of(2000, 1, 1), LocalDate.of(2100, 12, 31))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<FoodDiaryDTO> getByPatientAndDate(Long patientId, LocalDate date) {
        return foodDiaryRepository.findByPatientIdAndDate(patientId, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Integer getDailyCalories(Long patientId, LocalDate date) {
        return foodDiaryRepository.sumCaloriesByPatientAndDate(patientId, date);
    }

    public FoodDiaryDTO update(Long id, FoodDiaryDTO dto) {
        FoodDiary diary = foodDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodDiary entry not found"));

        if (dto.getPatientId() != null && !dto.getPatientId().equals(diary.getPatient().getId())) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            diary.setPatient(patient);
        }

        if (dto.getLifestylePlanId() != null) {
            LifestylePlan lifestylePlan = lifestylePlanRepository.findById(dto.getLifestylePlanId())
                    .orElseThrow(() -> new RuntimeException("LifestylePlan not found"));
            diary.setLifestylePlan(lifestylePlan);
        } else {
            diary.setLifestylePlan(null);
        }

        diary.setDate(dto.getDate());
        diary.setMealType(dto.getMealType());
        diary.setFoodName(dto.getFoodName());
        diary.setCalories(dto.getCalories());
        diary.setNotes(dto.getNotes());
        diary.setImageUrl(dto.getImageUrl());

        return toDTO(foodDiaryRepository.save(diary));
    }

    public void delete(Long id) {
        foodDiaryRepository.deleteById(id);
    }

    private FoodDiaryDTO toDTO(FoodDiary f) {
        return FoodDiaryDTO.builder()
                .id(f.getId())
                .patientId(f.getPatient().getId())
                .lifestylePlanId(f.getLifestylePlan() != null ? f.getLifestylePlan().getId() : null)
                .date(f.getDate())
                .mealType(f.getMealType())
                .foodName(f.getFoodName())
                .calories(f.getCalories())
                .notes(f.getNotes())
                .imageUrl(f.getImageUrl())
                .build();
    }
}
