package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LaboratoryStaff;
import com.aziz.demosec.dto.LaboratoryStaffRequest;
import com.aziz.demosec.dto.LaboratoryStaffResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LaboratoryStaffMapper {

    public LaboratoryStaff toEntity(LaboratoryStaffRequest dto) {
        LaboratoryStaff staff = new LaboratoryStaff();
        return staff;
    }

    public LaboratoryStaffResponse toDto(LaboratoryStaff staff) {
        return LaboratoryStaffResponse.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .laboratoryId(staff.getLaboratory() != null ? staff.getLaboratory().getId() : null)
                .build();
    }

    public List<LaboratoryStaff> toEntities(List<LaboratoryStaffRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<LaboratoryStaffResponse> toDtos(List<LaboratoryStaff> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(LaboratoryStaffRequest dto, LaboratoryStaff entity) {
        if (dto == null || entity == null) return;
    }
}
