package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LabResultMapper {

    public LabResult toEntity(LabResultRequest dto) {
        return LabResult.builder()
                .resultFile(dto.getResultFile())
                .resultData(dto.getResultData())
                .build();
    }

    public LabResultResponse toDto(LabResult labResult) {
        return LabResultResponse.builder()
                .id(labResult.getId())
                .labRequestId(labResult.getLabRequest() != null ? labResult.getLabRequest().getId() : null)
                .resultFile(labResult.getResultFile())
                .resultData(labResult.getResultData())
                .completedAt(labResult.getCompletedAt())
                .build();
    }

    public List<LabResult> toEntities(List<LabResultRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<LabResultResponse> toDtos(List<LabResult> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(LabResultRequest dto, LabResult entity) {
        if (dto == null || entity == null) return;
        entity.setResultFile(dto.getResultFile());
        entity.setResultData(dto.getResultData());
    }
}
