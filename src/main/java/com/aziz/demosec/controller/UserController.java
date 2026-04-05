package com.aziz.demosec.controller;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.UserResponseDTO;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

<<<<<<< HEAD
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
            Role r = Role.valueOf(role.toUpperCase()); // Convertit la String en Enum
            List<UserResponseDTO> users = userService.getByRole(r);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException ex) {
            // Si le rôle fourni n’existe pas dans l’Enum
            return ResponseEntity.badRequest().build();
=======
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
>>>>>>> origin/MedicalRecord
        }
        return userRepository.findByEmail(principal.getName())
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .phone(user.getPhone())
                        .birthDate(user.getBirthDate())
                        .photo(user.getPhoto())
                        .enabled(user.isEnabled())
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
