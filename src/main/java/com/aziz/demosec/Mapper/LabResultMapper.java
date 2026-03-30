package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LabResultMapper {

    public LabResult toEntity(LabResultRequest dto) {
        if (dto == null) {
            return null;
        }
        
        return LabResult.builder()
                .resultData(dto.getResultData())
                .abnormalFindings(dto.getAbnormalFindings())
                .technicianName(dto.getTechnicianName())
                .verifiedBy(dto.getVerifiedBy())
                .isAbnormal(dto.getIsAbnormal())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .build();
    }

    public static LabResultResponse toDto(LabResult labResult) {
        if (labResult == null) {
            return null;
        }
        
        LabResultResponse.LabResultResponseBuilder response = LabResultResponse.builder()
                .id(labResult.getId())
                .resultData(labResult.getResultData())
                .abnormalFindings(labResult.getAbnormalFindings())
                .technicianName(labResult.getTechnicianName())
                .verifiedBy(labResult.getVerifiedBy())
                .isAbnormal(labResult.getIsAbnormal())
                .completedAt(labResult.getCompletedAt())
                .verifiedAt(labResult.getVerifiedAt())
                .status(labResult.getStatus());

        if (labResult.getLabRequest() != null) {
            response.labRequestId(labResult.getLabRequest().getId());
            response.testType(labResult.getLabRequest().getTestType());
            if (labResult.getLabRequest().getPatient() != null) {
                response.patientName(labResult.getLabRequest().getPatient().getFullName());
            }
            if (labResult.getLabRequest().getLaboratory() != null) {
                response.laboratoryName(labResult.getLabRequest().getLaboratory().getName());
            }
        }
        
        return response.build();
    }

    public void updateFromDto(LabResultRequest dto, LabResult entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        if (dto.getResultData() != null) entity.setResultData(dto.getResultData());
        if (dto.getAbnormalFindings() != null) entity.setAbnormalFindings(dto.getAbnormalFindings());
        if (dto.getTechnicianName() != null) entity.setTechnicianName(dto.getTechnicianName());
        if (dto.getVerifiedBy() != null) entity.setVerifiedBy(dto.getVerifiedBy());
        if (dto.getIsAbnormal() != null) {
            entity.setIsAbnormal(dto.getIsAbnormal());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}
