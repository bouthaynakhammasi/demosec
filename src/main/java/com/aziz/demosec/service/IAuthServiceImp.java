package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.PatientRepository;
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
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public User register(RegisterRequest req) {

        if (req.email() == null || req.email().isBlank())
            throw new IllegalArgumentException("Email required");

        if (req.password() == null || req.password().length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters");

        if (req.role() == null)
            throw new IllegalArgumentException("Role required");

        if (userRepository.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException("Email already used");

        // =========================
        // CAS PATIENT
        // =========================
        if (req.role() == Role.PATIENT) {

            Patient patient = new Patient();
            patient.setFullName(req.fullName() == null ? "Not Available" : req.fullName());
            patient.setEmail(req.email());
            patient.setPassword(passwordEncoder.encode(req.password()));
            patient.setRole(Role.PATIENT);
            patient.setPhone(req.phone());
            patient.setBirthDate(req.birthDate());

            patient.setGender(req.gender());
            patient.setBloodType(req.bloodType());
            patient.setEmergencyContactName(req.emergencyContactName());
            patient.setEmergencyContactPhone(req.emergencyContactPhone());

            // ✅ NOUVEAUX CHAMPS AJOUTÉS
            patient.setChronicDiseases(req.chronicDiseases());
            patient.setDrugAllergies(req.drugAllergies());
            patient.setHereditaryDiseases(req.hereditaryDiseases());

            patient.setEnabled(true);

            return patientRepository.save(patient);
        }

        // =========================
        // CAS USER / DOCTOR / ADMIN
        // =========================
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
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());

        // Fetch the user to get the fullName
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        String token = jwtService.generateToken(userDetails, user.getFullName());

        return new AuthResponse(token, userDetails.getUsername(), user.getFullName(), role);
    }
}