package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.ProgressTracking;
import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;
import org.springframework.stereotype.Component;

@Component
public class ProgressTrackingMapper {

    public static ProgressTracking toEntity(ProgressTrackingRequest req) {
        if (req == null) return null;
        return ProgressTracking.builder()
                .date(req.getDate())
                .value(req.getValue())
                .notes(req.getNotes())
                .build();
    }

    public static ProgressTrackingResponse toResponse(ProgressTracking tracking) {
        if (tracking == null) return null;

        return ProgressTrackingResponse.builder()
                .id(tracking.getId())
                .patientId(tracking.getPatient() != null ? tracking.getPatient().getId() : null)
                .goalId(tracking.getGoal() != null ? tracking.getGoal().getId() : null)
                .date(tracking.getDate())
                .value(tracking.getValue())
                .notes(tracking.getNotes())
                .build();
    }
}
