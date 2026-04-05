package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.ConsultationMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String specialty;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private BigDecimal consultationFee;
    private ConsultationMode consultationMode;
    private String clinicAddress;
    private Long clinicId;
    private String clinicName;
    private boolean isProfileComplete;
    private String bio;
    private Integer patientCount;
    private Double rating;
    private String profilePicture;
}
