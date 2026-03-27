package com.aziz.demosec.dto;

import java.time.LocalDate;

public record PatientProfileUpdateRequest(
    String fullName,
    String email,
    String phone,
    LocalDate birthDate,
    String gender,
    String emergencyContactName,
    String emergencyContactPhone,
    Double height,
    Double weight,
    String bloodType,
    String allergies,
    String diseases,
    String photo
) {}
