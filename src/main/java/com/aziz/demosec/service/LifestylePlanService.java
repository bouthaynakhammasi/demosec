package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.Entities.LifestylePlan;
import com.aziz.demosec.Mapper.LifestylePlanMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
import com.aziz.demosec.repository.LifestylePlanRepository;
import com.aziz.demosec.repository.UserRepository;

import com.aziz.demosec.Entities.PlanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LifestylePlanService implements ILifestylePlanService {

    private final LifestylePlanRepository planRepository;
    private final LifestyleGoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LifestylePlanResponse addPlan(LifestylePlanRequest req) {

        LifestyleGoal goal = goalRepository.findById(req.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        User nutritionist = userRepository.findById(req.getNutritionistId())
                .orElseThrow(() -> new RuntimeException("Nutritionist not found"));

        LifestylePlan plan = LifestylePlanMapper.toEntity(req);

        plan.setGoal(goal);
        plan.setNutritionist(nutritionist);
        plan.setStatus(PlanStatus.ACTIVE);

        return LifestylePlanMapper.toResponse(planRepository.save(plan));
    }

    @Override
    public LifestylePlanResponse getPlanById(Long id) {
        return planRepository.findById(id)
                .map(LifestylePlanMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    @Override
    public List<LifestylePlanResponse> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(LifestylePlanMapper::toResponse)
                .toList();
    }

    @Override
    public List<LifestylePlanResponse> getPlansByPatientId(Long patientId) {
        return planRepository.findByGoal_Patient_Id(patientId)
                .stream()
                .map(LifestylePlanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LifestylePlanResponse updatePlan(Long id, LifestylePlanRequest req) {
        LifestylePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setTitle(req.getTitle());
        plan.setDescription(req.getDescription());
        plan.setStartDate(req.getStartDate());
        plan.setEndDate(req.getEndDate());

        return LifestylePlanMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }

    @Override
    public Long countAll() {
        return planRepository.count();
    }
}