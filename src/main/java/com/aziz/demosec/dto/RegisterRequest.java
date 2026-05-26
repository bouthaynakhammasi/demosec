package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.ConsultationMode;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{8,15}$", message = "Phone number must be between 8 and 15 digits")
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // --- Patient fields ---
    private Gender gender;
    private BloodType bloodType;
    private String emergencyContactName;
    @Pattern(regexp = "^\\d{8,15}$", message = "Emergency contact phone must be between 8 and 15 digits")
    private String emergencyContactPhone;
    private String glucoseRate;
    private String allergies;
    private String diseases;
    @PositiveOrZero(message = "Height must be positive")
    private Double height;
    @PositiveOrZero(message = "Weight must be positive")
    private Double weight;
    private String chronicDiseases;
    private String drugAllergies;
    private String hereditaryDiseases;
    private List<Map<String, String>> medicalHistories;

    // --- Doctor fields ---
    private String specialty;
    private String licenseNumber;
    @PositiveOrZero(message = "Consultation fee must be positive or zero")
    private BigDecimal consultationFee;
    private ConsultationMode consultationMode;

    // --- Clinic fields ---
    private String clinicName;
    private String clinicAddress;
    @Pattern(regexp = "^\\d{8,15}$", message = "Clinic phone must be between 8 and 15 digits")
    private String clinicPhone;
    @Pattern(regexp = "^\\d{3,15}$", message = "Emergency phone must be between 3 and 15 digits")
    private String emergencyPhone;
    @Pattern(regexp = "^\\d{3,15}$", message = "Ambulance phone must be between 3 and 15 digits")
    private String ambulancePhone;

    // --- Laboratory Staff fields ---
    private String labName;
    private String labAddress;
    @Pattern(regexp = "^\\d{8,15}$", message = "Lab phone must be between 8 and 15 digits")
    private String labPhone;

    // --- Pharmacist fields ---
    private String pharmacyName;
    private String pharmacyAddress;
    @Pattern(regexp = "^\\d{8,15}$", message = "Pharmacy phone must be between 8 and 15 digits")
    private String pharmacyPhone;
    @Email(message = "Invalid pharmacy email format")
    private String pharmacyEmail;

    // --- Service Provider fields ---
    private String certificationDocument;
    private List<String> homeCareServices;
    private List<Long> specialtyIds;

    // --- Other potential fields ---
    private String profileImage;
    private Long clinicId;
}
