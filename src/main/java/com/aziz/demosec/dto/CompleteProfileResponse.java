package com.aziz.demosec.dto;

public record CompleteProfileResponse(
        Long userId,
        String fullName,
        String email,
        String role,
        boolean profileCompleted,
        String profileImage
) {}
