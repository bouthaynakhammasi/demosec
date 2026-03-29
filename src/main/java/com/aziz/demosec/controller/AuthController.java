package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        log.info("Registering user: {}", req);
        try {
            var saved = authService.register(req);
            return ResponseEntity.ok("User created: " + saved.getEmail());
        } catch (Exception e) {
            log.error("Registration error for {}: {}", req.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}