package com.aziz.demosec.dto;


import com.aziz.demosec.Entities.ConsultationMode;
import java.math.BigDecimal;
import java.util.List;

public record CompleteProfileRequest(
        // DOCTOR fields
        String specialty,
        String licenseNumber,
        Integer yearsOfExperience,
        BigDecimal consultationFee,
        ConsultationMode consultationMode,

        // NUTRITIONIST fields
        String specialties,
        String bio,

        // HOME CARE PROVIDER (ServiceProvider entity)
        String certificationDocument,
        List<Long> serviceIds
) {}
