package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LabResultMapper {

    public LabResult toEntity(LabResultRequest dto) {
        if (dto == null) {
            return null;
        }
        
        return LabResult.builder()
                .resultFile(dto.getResultFile())
                .resultData(dto.getResultData())
                .testType(dto.getTestType())
                .normalValue(dto.getNormalValue())
                .abnormalFindings(dto.getAbnormalFindings())
                .technicianName(dto.getTechnicianName())
                .verifiedBy(dto.getVerifiedBy())
                .isAbnormal(dto.getIsAbnormal())
                .recommendations(dto.getRecommendations())
                .priority(dto.getPriority())
                .build();
    }

    public static LabResultResponse toDto(LabResult labResult) {
        if (labResult == null) {
            return null;
        }
        
        return LabResultResponse.builder()
                .id(labResult.getId())
                .labRequestId(labResult.getLabRequest() != null ? labResult.getLabRequest().getId() : null)
                .resultFile(labResult.getResultFile())
                .resultData(labResult.getResultData())
                .testType(labResult.getTestType())
                .normalValue(labResult.getNormalValue())
                .abnormalFindings(labResult.getAbnormalFindings())
                .technicianName(labResult.getTechnicianName())
                .verifiedBy(labResult.getVerifiedBy())
                .isAbnormal(labResult.getIsAbnormal())
                .recommendations(labResult.getRecommendations())
                .priority(labResult.getPriority())
                .completedAt(labResult.getCompletedAt())
                .verifiedAt(labResult.getVerifiedAt())
                .status(labResult.getStatus())
                .isUrgent(labResult.getIsUrgent())
                .resultCategory(labResult.getResultCategory())
                .build();
    }

    public void updateFromDto(LabResultRequest dto, LabResult entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        entity.setResultFile(dto.getResultFile());
        entity.setResultData(dto.getResultData());
        entity.setTestType(dto.getTestType());
        entity.setNormalValue(dto.getNormalValue());
        entity.setAbnormalFindings(dto.getAbnormalFindings());
        entity.setTechnicianName(dto.getTechnicianName());
        entity.setVerifiedBy(dto.getVerifiedBy());
        entity.setIsAbnormal(dto.getIsAbnormal());
        entity.setRecommendations(dto.getRecommendations());
        entity.setPriority(dto.getPriority());
    }
}
