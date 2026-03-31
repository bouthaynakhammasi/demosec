package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.ForgotPasswordRequest;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.service.IAuthService;
import com.aziz.demosec.dto.ResetPasswordRequest;

import com.aziz.demosec.service.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IFileStorageService fileStorageService;

    // POST /auth/register
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public org.springframework.http.ResponseEntity<?> register(
            @RequestParam("user") String userJson,
            @RequestParam(value = "document", required = false) org.springframework.web.multipart.MultipartFile document) {

        System.out.println("=== REGISTRATION ATTEMPT ===");
        System.out.println("User JSON received: " + userJson);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            RegisterRequest req = mapper.readValue(userJson, RegisterRequest.class);

            String documentUrl = null;
            if ((req.role() == com.aziz.demosec.domain.Role.PHARMACIST || req.role() == com.aziz.demosec.domain.Role.HOME_CARE_PROVIDER) && document != null && !document.isEmpty()) {
                System.out.println("Storing document: " + document.getOriginalFilename());
                String folderName = req.role() == com.aziz.demosec.domain.Role.PHARMACIST ? "pharmacist_docs" : "provider_docs";
                documentUrl = fileStorageService.storeFile(document, folderName);
            }

            var saved = authService.register(req, documentUrl);
            return org.springframework.http.ResponseEntity.ok("User created: " + saved.getEmail());
        } catch (Exception e) {
            System.err.println("Registration failed error detail: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // ✅ Forgot Password → envoie email avec lien
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            authService.forgotPassword(req.email());
            return ResponseEntity.ok("Reset email sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Reset Password → change le mot de passe
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            authService.resetPassword(req.token(), req.newPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}