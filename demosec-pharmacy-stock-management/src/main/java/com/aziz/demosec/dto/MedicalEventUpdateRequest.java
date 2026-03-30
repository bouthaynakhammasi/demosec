package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDate;

@Data
public class MedicalEventUpdateRequest {

    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Future(message = "Event date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String imageUrl;

    @Size(max = 150, message = "Venue name must not exceed 150 characters")
    private String venueName;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 30, message = "Postal code must not exceed 30 characters")
    private String postalCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;

    @Size(max = 100, message = "Platform name must not exceed 100 characters")
    private String platformName;

    @Size(max = 500, message = "Meeting link must not exceed 500 characters")
    private String meetingLink;

    @Size(max = 100, message = "Meeting password must not exceed 100 characters")
    private String meetingPassword;
}