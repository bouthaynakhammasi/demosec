package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.LifestylePlan;
import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;
import org.springframework.stereotype.Component;

@Component
public class LifestylePlanMapper {

    public static LifestylePlan toEntity(LifestylePlanRequest req) {
        if (req == null) return null;
        return LifestylePlan.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();
    }

    public static LifestylePlanResponse toResponse(LifestylePlan plan) {
        if (plan == null) return null;

        return LifestylePlanResponse.builder()
                .id(plan.getId())
                .goalId(plan.getGoal() != null ? plan.getGoal().getId() : null)
                .nutritionistId(plan.getNutritionist() != null ? plan.getNutritionist().getId() : null)
                .title(plan.getTitle())
                .description(plan.getDescription())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .status(plan.getStatus() != null ? plan.getStatus().name() : null)
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
