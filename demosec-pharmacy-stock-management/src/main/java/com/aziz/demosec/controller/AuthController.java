package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User created: " + saved.getEmail());
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
