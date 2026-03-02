package com.aziz.demosec.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalEventUpdateRequest {
    private String title;
    private String description;
    private LocalDateTime date;

    // fields spécifiques (optionnel)
    private String venueName;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Integer capacity;

    private String platformName;
    private String meetingLink;
    private String meetingPassword;
}