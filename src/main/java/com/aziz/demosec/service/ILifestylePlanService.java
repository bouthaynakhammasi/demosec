package com.aziz.demosec.service;

import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;

import java.util.List;

public interface ILifestylePlanService {
    LifestylePlanResponse addPlan(LifestylePlanRequest request);
    LifestylePlanResponse getPlanById(Long id);
    List<LifestylePlanResponse> getAllPlans();
    List<LifestylePlanResponse> getPlansByPatientId(Long patientId);
    LifestylePlanResponse updatePlan(Long id, LifestylePlanRequest request);
    void deletePlan(Long id);
    Long countAll();
}
