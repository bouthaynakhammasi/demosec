package com.aziz.demosec.controller;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthenticated");
        }
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Return shape matching Angular UserResponseDTO
            Map<String, Object> response = Map.of(
                    "id", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "enabled", user.isEnabled(),
                    "phone", user.getPhone() != null ? user.getPhone() : "",
                    "birthDate", user.getBirthDate() != null ? user.getBirthDate() : ""
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
