package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.ForgotPasswordRequest;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.service.AuthService;
import com.aziz.demosec.dto.ResetPasswordRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final com.aziz.demosec.service.FileStorageService fileStorageService;

    // POST /auth/register
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> register(
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
            return ResponseEntity.ok("User created: " + saved.getEmail());
        } catch (Exception e) {
            System.err.println("Registration failed error detail: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Your account is pending admin approval. Please wait."));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again."));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            authService.forgotPassword(req.email());
            return ResponseEntity.ok("Reset email sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            authService.resetPassword(req.token(), req.newPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Reset password error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
}
