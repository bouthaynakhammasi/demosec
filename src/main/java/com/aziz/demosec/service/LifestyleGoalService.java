package com.aziz.demosec.service;

import com.aziz.demosec.Entities.GoalStatus;
import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Mapper.LifestyleGoalMapper;
import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LifestyleGoalService implements ILifestyleGoalService {

    private final LifestyleGoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LifestyleGoalResponse addGoal(LifestyleGoalRequest req) {
        LifestyleGoal goal = LifestyleGoalMapper.toGoalEntity(req);

        goal.setPatient(
                userRepository.findById(req.getPatientId())
                        .orElseThrow(() -> new RuntimeException("Patient not found"))
        );

        goal.setStatus(GoalStatus.IN_PROGRESS);

        return LifestyleGoalMapper.toGoalResponse(goalRepository.save(goal));
    }

    @Override
    public LifestyleGoalResponse getGoalById(Long id) {
        return goalRepository.findById(id)
                .map(LifestyleGoalMapper::toGoalResponse)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    @Override
    public List<LifestyleGoalResponse> getAllGoals() {
        return goalRepository.findAll()
                .stream()
                .map(LifestyleGoalMapper::toGoalResponse)
                .toList();
    }

    @Override
    public List<LifestyleGoalResponse> getGoalsByPatientId(Long patientId) {
        return goalRepository.findByPatientId(patientId)
                .stream()
                .map(LifestyleGoalMapper::toGoalResponse)
                .toList();
    }

    @Override
    @Transactional
    public LifestyleGoalResponse updateGoal(Long id, LifestyleGoalRequest req) {
        LifestyleGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setCategory(LifestyleGoalMapper.parseCategory(req.getCategory()));
        goal.setTargetValue(req.getTargetValue());
        goal.setBaselineValue(req.getBaselineValue());
        goal.setTargetDate(req.getTargetDate());

        return LifestyleGoalMapper.toGoalResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}