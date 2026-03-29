package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryResponse;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryMapper {

    public static LaboratoryResponse toResponse(Laboratory laboratory) {
        if (laboratory == null) {
            return null;
        }
        return LaboratoryResponse.builder()
                .id(laboratory.getId())
                .name(laboratory.getName())
                .address(laboratory.getAddress())
                .phone(laboratory.getPhone())
                .build();
    }
}
