package com.aziz.demosec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record ClinicProfileResponse(
    Long id,
    String fullName,
    String email,
    String phone,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate,
    String photo,
    String clinicName,
    String address,
    Double latitude,
    Double longitude,
    boolean hasEmergency,
    boolean hasAmbulance,
    String emergencyPhone,
    String ambulancePhone
) {}
