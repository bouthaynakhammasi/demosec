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
public class IAuthServiceImp implements IAuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    // ── Register ──────────────────────────────────────────
    @Override
    public User register(RegisterRequest req) {

        // 1. Valider email
        if (req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("Email required");
        }

        // 2. Valider password
        if (req.password() == null || req.password().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }

        // 3. Valider role
        if (req.role() == null) {
            throw new IllegalArgumentException("Role required");
        }

        // 4. Vérifier email unique
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        // 5. Créer et sauvegarder l'utilisateur
        User u = User.builder()
                .fullName(req.fullName() == null ? "Not Available" : req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .enabled(true)
                .build();

        return userRepository.save(u);
    }

    // ── Login ─────────────────────────────────────────────
    @Override
    public AuthResponse login(LoginRequest req) {

        // 1. Authentifier email + password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(), req.password()
                )
        );

        // 2. Charger les détails utilisateur
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(req.email());

        // 3. Générer le token JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Extraire le rôle
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() ->
                        new IllegalStateException("No roles found"));

        // 5. Retourner la réponse
        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}
