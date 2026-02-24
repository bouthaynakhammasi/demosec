package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabTest;
import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LabTestMapper {

    public LabTest toEntity(LabTestRequest dto) {
        return LabTest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }

    public LabTestResponse toDto(LabTest labTest) {
        return LabTestResponse.builder()
                .id(labTest.getId())
                .name(labTest.getName())
                .description(labTest.getDescription())
                .price(labTest.getPrice())
                .laboratoryId(labTest.getLaboratory() != null ? labTest.getLaboratory().getId() : null)
                .laboratoryName(labTest.getLaboratory() != null ? labTest.getLaboratory().getName() : null)
                .build();
    }

    public List<LabTest> toEntities(List<LabTestRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<LabTestResponse> toDtos(List<LabTest> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(LabTestRequest dto, LabTest entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
    }
}
