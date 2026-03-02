package com.aziz.demosec.dto;

public record AuthResponse(
        String token,
        String email,
        String role) {
}
