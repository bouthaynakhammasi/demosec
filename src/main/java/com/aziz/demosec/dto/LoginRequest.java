package com.aziz.demosec.dto;

public record LoginRequest(
        String email,
        String password) {
}
