package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LabResultMapper {

    public LabResult toEntity(LabResultRequest dto) {
        if (dto == null) return null;

        return LabResult.builder()
                .resultData(dto.getResultData())
                .technicianName(dto.getTechnicianName())
                .isAbnormal(dto.getIsAbnormal())
                .status(dto.getStatus())
                .abnormalFindings(dto.getAbnormalFindings())
                .verifiedBy(dto.getVerifiedBy())
                .build();
    }

    public static LabResultResponse toDto(LabResult entity) {
        if (entity == null) return null;

        return LabResultResponse.builder()
                .id(entity.getId())
                .labRequestId(entity.getLabRequest() != null ?
                        entity.getLabRequest().getId() : null)
                .patientName(entity.getLabRequest() != null &&
                        entity.getLabRequest().getPatient() != null ?
                        entity.getLabRequest().getPatient().getFullName() : null)
                .laboratoryName(entity.getLabRequest() != null &&
                        entity.getLabRequest().getLaboratory() != null ?
                        entity.getLabRequest().getLaboratory().getName() : null)
                .testType(entity.getLabRequest() != null ?
                        entity.getLabRequest().getTestType() : null)
                .resultData(entity.getResultData())
                .technicianName(entity.getTechnicianName())
                .verifiedBy(entity.getVerifiedBy())
                .abnormalFindings(entity.getAbnormalFindings())
                .status(entity.getStatus())
                .isAbnormal(entity.getIsAbnormal())
                .completedAt(entity.getCompletedAt())
                .verifiedAt(entity.getVerifiedAt())
                .build();
    }

    public void updateFromDto(LabResultRequest dto, LabResult entity) {
        if (dto == null || entity == null) return;

        entity.setResultData(dto.getResultData());
        entity.setTechnicianName(dto.getTechnicianName());
        entity.setIsAbnormal(dto.getIsAbnormal());
        entity.setStatus(dto.getStatus());
        entity.setAbnormalFindings(dto.getAbnormalFindings());
        entity.setVerifiedBy(dto.getVerifiedBy());
    }
}