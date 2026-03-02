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
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public User register(RegisterRequest req) {
        if (req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }
        if (req.role() == null) {
            throw new IllegalArgumentException("Role required");
        }
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        User u = User.builder()
                .fullName(req.fullName() == null ? "Not Available" : req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .phone(req.phone())
                .birthDate(req.birthDate())
                .enabled(true)
                .build();

        return userRepository.save(u);
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

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}
