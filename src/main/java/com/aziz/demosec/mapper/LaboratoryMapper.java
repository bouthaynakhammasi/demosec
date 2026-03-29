package com.aziz.demosec.mapper;
import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import org.springframework.stereotype.Component;
@Component
public class LaboratoryMapper {
    public Laboratory toEntity(LaboratoryRequest request) {
        return Laboratory.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingHours(request.getOpeningHours())
                .specializations(request.getSpecializations())
                .active(true)
                .build();
    }
    public LaboratoryResponse toDto(Laboratory lab) {
        LaboratoryResponse response = new LaboratoryResponse();
        response.setId(lab.getId());
        response.setName(lab.getName());
        response.setAddress(lab.getAddress());
        response.setPhone(lab.getPhone());
        response.setEmail(lab.getEmail());
        response.setOpeningHours(lab.getOpeningHours());
        response.setSpecializations(lab.getSpecializations());
        response.setActive(lab.isActive());
        return response;
    }
    public void updateFromDto(LaboratoryRequest request, Laboratory lab) {
        lab.setName(request.getName());
        lab.setAddress(request.getAddress());
        lab.setPhone(request.getPhone());
        lab.setEmail(request.getEmail());
        lab.setOpeningHours(request.getOpeningHours());
        lab.setSpecializations(request.getSpecializations());
    }
}