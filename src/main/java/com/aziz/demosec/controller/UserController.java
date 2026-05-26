package com.aziz.demosec.controller;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.user.ChangePasswordDTO;
import com.aziz.demosec.dto.user.UserRequestDTO;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.dto.PasswordChangeRequest;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserService userService;

    // --- User Profile ---
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

    // --- Password Management ---
    @PutMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(Principal principal, @RequestBody PasswordChangeRequest request) {
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

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePasswordById(@PathVariable("id") Long id,
                                                @Valid @RequestBody ChangePasswordDTO dto) {
        try {
            userService.changePassword(id, dto);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- Admin CRUD ---
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(201).body(userService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleEnabled(@PathVariable("id") Long id) {
        userService.toggleEnabled(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getByRole(@PathVariable("role") String role) {
        try {
            Role r = Role.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userService.getByRole(r));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}