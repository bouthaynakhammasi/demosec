package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LabRequestMapper {

    public LabRequest toEntity(LabRequestRequest dto) {
        return LabRequest.builder()
                .status(dto.getStatus())
                .build();
    }

    public LabRequestResponse toDto(LabRequest labRequest) {
        return LabRequestResponse.builder()
                .id(labRequest.getId())
                .patientId(labRequest.getPatient() != null ? labRequest.getPatient().getId() : null)
                .doctorId(labRequest.getDoctor() != null ? labRequest.getDoctor().getId() : null)
                .laboratoryId(labRequest.getLaboratory() != null ? labRequest.getLaboratory().getId() : null)
                .status(labRequest.getStatus())
                .requestedAt(labRequest.getRequestedAt())
                .build();
    }

    public List<LabRequest> toEntities(List<LabRequestRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<LabRequestResponse> toDtos(List<LabRequest> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(LabRequestRequest dto, LabRequest entity) {
        if (dto == null || entity == null) return;
        entity.setStatus(dto.getStatus());
    }
}
