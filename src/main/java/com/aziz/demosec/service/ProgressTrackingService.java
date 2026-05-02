package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.Entities.LifestylePlan;
import com.aziz.demosec.Entities.ProgressTracking;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Mapper.ProgressTrackingMapper;
import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
import com.aziz.demosec.repository.LifestylePlanRepository;
import com.aziz.demosec.repository.ProgressTrackingRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressTrackingService implements IProgressTrackingService {

    private final ProgressTrackingRepository progressRepository;
    private final LifestyleGoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProgressTrackingResponse addTracking(ProgressTrackingRequest req) {

        LifestyleGoal goal = goalRepository.findById(req.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        User patient = userRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        ProgressTracking progress = ProgressTrackingMapper.toEntity(req);

        progress.setGoal(goal);
        progress.setPatient(patient);

        return ProgressTrackingMapper.toResponse(progressRepository.save(progress));
    }

    @Override
    public ProgressTrackingResponse getTrackingById(Long id) {
        return progressRepository.findById(id)
                .map(ProgressTrackingMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Progress record not found"));
    }

    @Override
    public List<ProgressTrackingResponse> getAllTrackings() {
        return progressRepository.findAll()
                .stream()
                .map(ProgressTrackingMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProgressTrackingResponse> getTrackingsByPatientId(Long patientId) {
        return progressRepository.findByPatientId(patientId)
                .stream()
                .map(ProgressTrackingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProgressTrackingResponse updateTracking(Long id, ProgressTrackingRequest req) {
        ProgressTracking progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress record not found"));

        progress.setDate(req.getDate());
        progress.setValue(req.getValue());
        progress.setNotes(req.getNotes());

        return ProgressTrackingMapper.toResponse(progressRepository.save(progress));
    }

    @Override
    @Transactional
    public void deleteTracking(Long id) {
        progressRepository.deleteById(id);
    }

    // ✅ Calculate Progress %
    public double calculateProgress(Long goalId) {

        LifestyleGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        List<ProgressTracking> records = progressRepository.findByGoalId(goalId);

        if (records.isEmpty()) return 0;

        BigDecimal latestValue = records.get(records.size() - 1).getValue();
        BigDecimal targetValue = goal.getTargetValue();
        BigDecimal baselineValue = goal.getBaselineValue();

        if (targetValue.compareTo(baselineValue) == 0) return 100.0;

        BigDecimal progress = latestValue.subtract(baselineValue)
                .divide(targetValue.subtract(baselineValue), 4, RoundingMode.HALF_UP);

        return Math.min(100.0, Math.max(0, progress.doubleValue() * 100));
    }
}