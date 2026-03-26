package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabTest;
import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import org.springframework.stereotype.Component;

@Component
public class LabTestMapper {

    public LabTest toEntity(LabTestRequest dto) {
        return LabTest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .testType(dto.getTestType())
                .durationMinutes(dto.getDurationMinutes())
                .genderSpecific(dto.getGenderSpecific())
                .requiresAppointment(
                        dto.getRequiresAppointment() != null
                                ? dto.getRequiresAppointment() : false)
                .requiresFasting(
                        dto.getRequiresFasting() != null
                                ? dto.getRequiresFasting() : false)
                .build();
    }

    public LabTestResponse toDto(LabTest e) {
        return LabTestResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .laboratoryId(
                        e.getLaboratory() != null
                                ? e.getLaboratory().getId() : null)
                .laboratoryName(
                        e.getLaboratory() != null
                                ? e.getLaboratory().getName() : null)
                .category(e.getCategory())
                .testType(e.getTestType())
                .durationMinutes(e.getDurationMinutes())
                .genderSpecific(e.getGenderSpecific())
                .requiresAppointment(e.getRequiresAppointment())
                .requiresFasting(e.getRequiresFasting())
                .build();
    }

    public void updateFromDto(LabTestRequest dto, LabTest entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setCategory(dto.getCategory());
        entity.setTestType(dto.getTestType());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setGenderSpecific(dto.getGenderSpecific());
        entity.setRequiresAppointment(
                dto.getRequiresAppointment() != null
                        ? dto.getRequiresAppointment() : false);
        entity.setRequiresFasting(
                dto.getRequiresFasting() != null
                        ? dto.getRequiresFasting() : false);
    }
}