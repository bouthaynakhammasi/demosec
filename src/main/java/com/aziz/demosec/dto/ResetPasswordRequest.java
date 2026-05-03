package com.aziz.demosec.dto;

public record ResetPasswordRequest(String token, String newPassword) {}