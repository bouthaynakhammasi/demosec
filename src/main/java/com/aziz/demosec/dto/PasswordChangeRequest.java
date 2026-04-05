package com.aziz.demosec.dto;

public record PasswordChangeRequest(
    String currentPassword,
    String newPassword
) {}
