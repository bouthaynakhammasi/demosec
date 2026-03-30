package com.aziz.demosec.controller;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.user.UserResponseDTO;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LegacyUserController {

    private final com.aziz.demosec.repository.UserRepository userRepository;
    private final com.aziz.demosec.service.IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getLegacyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .enabled(user.isEnabled())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateLegacyProfile(Authentication authentication, @org.springframework.web.bind.annotation.RequestBody com.aziz.demosec.dto.user.UserRequestDTO dto) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        return ResponseEntity.ok(userService.update(user.getId(), dto));
    }
}
