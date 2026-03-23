package com.aziz.demosec.controller;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // ✅ Fix: endpoint appelé par le frontend Angular
    @GetMapping("/user/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getByEmail(authentication.getName()));
    }

    @PutMapping("/user/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateByEmail(authentication.getName(), dto));
    }

    // --- Endpoints /api/users ---
    @PostMapping("/api/users")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
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