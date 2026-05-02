package com.aziz.demosec.dto;

import com.aziz.demosec.Entities.MedicalEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalEventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private MedicalEventType eventType;
    private Long createdById;
    
    // Physical Event Details
    private String venueName;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Integer capacity;
    private String venueType;
    
    // Online Event Details
    private String platformName;
    private String meetingLink;
    private String meetingPassword;
    
    private String imageUrl;
    private Double ticketPrice;
}
