package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.ConsultationMode;
import com.aziz.demosec.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterRequest {
    // --- Common Fields ---
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private String phone;
    private String birthDate; // Using String to match User entity birthDate (sometimes LocalDate, but user said String)

    // --- Patient fields ---
    private Gender gender;
    private BloodType bloodType;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String glucoseRate;
    private String allergies;
    private String diseases;
    private Double height;
    private Double weight;

    // --- Doctor fields ---
    private String specialty;
    private String licenseNumber;
    private BigDecimal consultationFee;
    private ConsultationMode consultationMode;

    // --- Clinic fields ---
    private String clinicName;
    private String clinicAddress;
    private String clinicPhone;
    private String emergencyPhone;
    private String ambulancePhone;

    // --- Laboratory Staff fields ---
    private String labName;
    private String labAddress;
    private String labPhone;

    // --- Pharmacist fields ---
    private String pharmacyName;
    private String pharmacyAddress;
    private String pharmacyPhone;
    private String pharmacyEmail;

    // --- Service Provider fields ---
    private String certificationDocument;
    private List<String> homeCareServices;
    
    // --- Other potential fields ---
    private String profileImage;
    private Long clinicId;
}