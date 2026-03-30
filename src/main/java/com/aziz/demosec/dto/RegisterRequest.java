package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.BloodType;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.ToString;

@ToString
public class RegisterRequest {

    // Common fields
    private String fullName;
    private String email;
    private String password;
    private String role; // Use String for robust JSON parsing
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String profileImage;


    // Patient Fields
    private String gender; // Use String for robust JSON parsing
    private String bloodType; // Use String for robust JSON parsing
    private String emergencyContactName;
    private String emergencyContactPhone;
    private List<MedicalHistoryDTO> medicalHistories;

    // Provider / General fields
    private String specialty;
    private String licenseNumber;
    private Double consultationFee;
    private String consultationMode;
    private Long clinicId;

    // Clinic Fields
    private String clinicName;
    private String clinicAddress;
    private String clinicPhone;
    private String emergencyPhone;
    private String ambulancePhone;

    // Pharmacy Fields
    private String pharmacyName;
    private String pharmacyAddress;
    private String pharmacyPhone;
    private String pharmacyEmail;

    // Laboratory Fields
    private String labName;
    private String labAddress;
    private String labPhone;

    // Home Care Fields
    private String certificationDocument;
    private List<String> homeCareServices;

    public RegisterRequest() {}

    // Getters and Setters explicitly to avoid any Lombok issues in this environment
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public List<MedicalHistoryDTO> getMedicalHistories() { return medicalHistories; }
    public void setMedicalHistories(List<MedicalHistoryDTO> medicalHistories) { this.medicalHistories = medicalHistories; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    public String getConsultationMode() { return consultationMode; }
    public void setConsultationMode(String consultationMode) { this.consultationMode = consultationMode; }
    public Long getClinicId() { return clinicId; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
    public String getClinicAddress() { return clinicAddress; }
    public void setClinicAddress(String clinicAddress) { this.clinicAddress = clinicAddress; }
    public String getClinicPhone() { return clinicPhone; }
    public void setClinicPhone(String clinicPhone) { this.clinicPhone = clinicPhone; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public String getAmbulancePhone() { return ambulancePhone; }
    public void setAmbulancePhone(String ambulancePhone) { this.ambulancePhone = ambulancePhone; }
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
    public String getPharmacyAddress() { return pharmacyAddress; }
    public void setPharmacyAddress(String pharmacyAddress) { this.pharmacyAddress = pharmacyAddress; }
    public String getPharmacyPhone() { return pharmacyPhone; }
    public void setPharmacyPhone(String pharmacyPhone) { this.pharmacyPhone = pharmacyPhone; }
    public String getPharmacyEmail() { return pharmacyEmail; }
    public void setPharmacyEmail(String pharmacyEmail) { this.pharmacyEmail = pharmacyEmail; }
    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }
    public String getLabAddress() { return labAddress; }
    public void setLabAddress(String labAddress) { this.labAddress = labAddress; }
    public String getLabPhone() { return labPhone; }
    public void setLabPhone(String labPhone) { this.labPhone = labPhone; }
    public String getCertificationDocument() { return certificationDocument; }
    public void setCertificationDocument(String certificationDocument) { this.certificationDocument = certificationDocument; }
    public List<String> getHomeCareServices() { return homeCareServices; }
    public void setHomeCareServices(List<String> homeCareServices) { this.homeCareServices = homeCareServices; }

    @ToString
    public static class MedicalHistoryDTO {
        private String type;
        private String description;
        public MedicalHistoryDTO() {}
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}

