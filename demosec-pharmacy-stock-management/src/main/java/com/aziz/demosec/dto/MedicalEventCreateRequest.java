package com.aziz.demosec.dto.request;

import com.aziz.demosec.entities.MedicalEventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalEventCreateRequest {

    private String title;
    private String description;
    private LocalDateTime date;

    private MedicalEventType eventType;

    // common (optionnel si tu veux)
    private Long createdById;

    // PHYSICAL fields
    private String venueName;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Integer capacity;

    // ONLINE fields
    private String platformName;
    private String meetingLink;
    private String meetingPassword;
}
