package com.aziz.demosec.service;

import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;

import java.util.List;

public interface ILifestyleGoalService {
    LifestyleGoalResponse addGoal(LifestyleGoalRequest request);
    LifestyleGoalResponse getGoalById(Long id);
    List<LifestyleGoalResponse> getAllGoals();
    List<LifestyleGoalResponse> getGoalsByPatientId(Long patientId);
    LifestyleGoalResponse updateGoal(Long id, LifestyleGoalRequest request);
    void deleteGoal(Long id);
}
