package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.ForgotPasswordRequest;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.dto.ResetPasswordRequest;
import com.aziz.demosec.service.IAuthService;
import com.aziz.demosec.service.IFileStorageService;
import com.aziz.demosec.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;
    private final IFileStorageService fileStorageService;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> register(
            @RequestParam("user") String userJson,
            @RequestParam(value = "document", required = false) MultipartFile document) {

        log.info("Registration attempt with JSON: {}", userJson);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            RegisterRequest req = mapper.readValue(userJson, RegisterRequest.class);

            String documentUrl = null;
            if ((req.getRole() == Role.PHARMACIST || req.getRole() == Role.HOME_CARE_PROVIDER) && document != null && !document.isEmpty()) {
                String folderName = req.getRole() == Role.PHARMACIST ? "pharmacist_docs" : "provider_docs";
                documentUrl = fileStorageService.storeFile(document, folderName);
            }

            var saved = authService.register(req, documentUrl);
            return ResponseEntity.ok(Map.of("message", "User created successfully", "email", saved.getEmail()));
        } catch (Exception e) {
            log.error("Registration failed: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        try {
            authService.forgotPassword(req.email());
            return ResponseEntity.ok(Map.of("message", "Reset email sent successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Forgot password error: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        try {
            authService.resetPassword(req.token(), req.newPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Reset password error: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}