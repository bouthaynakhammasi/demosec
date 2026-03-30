package com.aziz.demosec.controller;


import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.repository.UserRepository;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.service.IUserService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.List;
@RestController
@RequestMapping("/user")

@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final IUserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody com.aziz.demosec.dto.PasswordChangeRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return userRepository.findByEmail(principal.getName())
                .map(user -> {
                    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Incorrect current password"));
                    }
                    user.setPassword(passwordEncoder.encode(request.newPassword()));
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
@GetMapping("/profile")
public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
    return ResponseEntity.ok(userService.getByEmail(authentication.getName()));
}

@PutMapping("/profile")
public ResponseEntity<UserResponseDTO> updateProfile(
        Authentication authentication,
        @Valid @RequestBody UserRequestDTO dto) {
    return ResponseEntity.ok(userService.updateByEmail(authentication.getName(), dto));
}

// --- Endpoints /api/users ---
@PostMapping("/api/users")
public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
    return ResponseEntity.status(201).body(userService.create(dto));
}

@GetMapping("/api/users/{id}")
public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getById(id));
}

@GetMapping("/api/users")
public ResponseEntity<List<UserResponseDTO>> getAll() {
    return ResponseEntity.ok(userService.getAll());
}

@PutMapping("/api/users/{id}")
public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody UserRequestDTO dto) {
    return ResponseEntity.ok(userService.update(id, dto));
}

@DeleteMapping("/api/users/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
}

@PatchMapping("/api/users/{id}/toggle")
public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
    userService.toggleEnabled(id);
    return ResponseEntity.noContent().build();
}

@GetMapping("/api/users/role/{role}")
public ResponseEntity<List<UserResponseDTO>> getByRole(@PathVariable String role) {
    try {
        Role r = Role.valueOf(role.toUpperCase());
        return ResponseEntity.ok(userService.getByRole(r));
    } catch (IllegalArgumentException ex) {
        return ResponseEntity.badRequest().build();
    }
    }
}