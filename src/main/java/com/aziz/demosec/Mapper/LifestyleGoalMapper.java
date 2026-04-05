package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.GoalCategory;
import com.aziz.demosec.Entities.GoalStatus;
import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;
import org.springframework.stereotype.Component;


@Component
public class LifestyleGoalMapper {

    public static LifestyleGoal toGoalEntity(LifestyleGoalRequest req) {
        if (req == null) return null;
        return LifestyleGoal.builder()
                .category(parseCategory(req.getCategory()))
                .targetValue(req.getTargetValue())
                .baselineValue(req.getBaselineValue())
                .targetDate(req.getTargetDate())
                .build();
    }

    public static LifestyleGoalResponse toGoalResponse(LifestyleGoal goal) {
        if (goal == null) return null;
        return LifestyleGoalResponse.builder()
                .id(goal.getId())
                .patientId(goal.getPatient() != null ? goal.getPatient().getId() : null)
                .category(goal.getCategory() != null ? goal.getCategory().name() : null)
                .targetValue(goal.getTargetValue())
                .baselineValue(goal.getBaselineValue())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus() != null ? goal.getStatus().name() : null)
                .plans(goal.getPlans() != null ? goal.getPlans()
                        .stream()
                        .map(LifestylePlanMapper::toResponse)
                        .toList() : null)
                .build();
    }

    public static GoalCategory parseCategory(String category) {
        if (category == null) return null;
        try {
            return GoalCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GoalCategory.GENERAL_HEALTH;
        }
    }

    public static GoalStatus parseStatus(String status) {
        if (status == null) return GoalStatus.IN_PROGRESS;
        try {
            return GoalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GoalStatus.IN_PROGRESS;
        }
    }
}
