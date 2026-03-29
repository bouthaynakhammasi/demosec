package com.aziz.demosec.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyCodeRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must contain 6 digits")
    private String code;
}