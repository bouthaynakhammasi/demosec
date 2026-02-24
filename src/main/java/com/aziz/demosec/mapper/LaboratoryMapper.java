package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LaboratoryMapper {

    public Laboratory toEntity(LaboratoryRequest dto) {
        return Laboratory.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();
    }

    public LaboratoryResponse toDto(Laboratory laboratory) {
        return LaboratoryResponse.builder()
                .id(laboratory.getId())
                .name(laboratory.getName())
                .address(laboratory.getAddress())
                .phone(laboratory.getPhone())
                .build();
    }

    public List<Laboratory> toEntities(List<LaboratoryRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<LaboratoryResponse> toDtos(List<Laboratory> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(LaboratoryRequest dto, Laboratory entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
    }
}
