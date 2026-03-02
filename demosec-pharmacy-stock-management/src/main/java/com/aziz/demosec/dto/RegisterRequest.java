package com.aziz.demosec.dto;

import com.aziz.demosec.domain.Role;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        Role role,
        String phone,
        String birthDate) {
}
