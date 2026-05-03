package com.aziz.demosec.dto;

import java.time.LocalDate;

public record ClinicProfileUpdateRequest(
    String fullName,
    String phone,
    LocalDate birthDate,
    String photo,
    String clinicName,
    String address,
    Double latitude,
    Double longitude,
    Boolean hasEmergency,
    Boolean hasAmbulance,
    String emergencyPhone,
    String ambulancePhone
) {}
