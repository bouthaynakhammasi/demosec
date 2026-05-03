package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public User register(RegisterRequest req) {
        try {
            if (req.getEmail() == null || req.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email required");
            }
            if (req.getPassword() == null || req.getPassword().length() < 6) {
                throw new IllegalArgumentException("Password must contain at least 6 characters");
            }
            if (req.getRole() == null) {
                throw new IllegalArgumentException("Role required");
            }
            if (userRepository.findByEmail(req.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already used");
            }

            String birthDateStr = req.getBirthDate() != null ? req.getBirthDate().toString() : null;
            com.aziz.demosec.domain.Role userRole;
            try {
                userRole = com.aziz.demosec.domain.Role.valueOf(req.getRole());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + req.getRole());
            }

            User u;
            if (userRole == com.aziz.demosec.domain.Role.PHARMACIST) {
                com.aziz.demosec.entities.Pharmacist p = new com.aziz.demosec.entities.Pharmacist();
                p.setFullName(req.getFullName() == null ? "Not Available" : req.getFullName());
                p.setEmail(req.getEmail());
                p.setPassword(passwordEncoder.encode(req.getPassword()));
                p.setRole(userRole);
                p.setPhone(req.getPhone());
                p.setBirthDate(birthDateStr);
                p.setEnabled(true);
                
                p.setPharmacyName(req.getPharmacyName());
                p.setPharmacyAddress(req.getPharmacyAddress());
                p.setPharmacyPhone(req.getPharmacyPhone());
                p.setPharmacyEmail(req.getPharmacyEmail());
                p.setDiplomaDocument(req.getDiplomaDocument());
                p.setPharmacySetupCompleted(true);
                p.setStatus(com.aziz.demosec.entities.PharmacistStatus.PENDING);
                u = p;
            } else {
                u = User.builder()
                        .fullName(req.getFullName() == null ? "Not Available" : req.getFullName())
                        .email(req.getEmail())
                        .password(passwordEncoder.encode(req.getPassword()))
                        .role(userRole)
                        .phone(req.getPhone())
                        .birthDate(birthDateStr)
                        .enabled(true)
                        .build();
            }

            return userRepository.save(u);
        } catch (Exception e) {
            log.error("=== REGISTER ERROR ===", e);
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        String token = jwtService.generateToken(userDetails, role);

        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}
