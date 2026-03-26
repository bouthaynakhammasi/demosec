package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LaboratoryStaff;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.domain.PasswordResetToken;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LaboratoryStaffRepository;
import com.aziz.demosec.repository.PasswordResetTokenRepository;
import com.aziz.demosec.repository.PatientRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryStaffRepository laboratoryStaffRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

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

        if (req.role() == Role.PATIENT) {
            Patient patient = new Patient();
            patient.setFullName(req.fullName() == null ? "Not Available" : req.fullName());
            patient.setEmail(req.email());
            patient.setPassword(passwordEncoder.encode(req.password()));
            patient.setRole(Role.PATIENT);
            patient.setPhone(req.phone());
            patient.setBirthDate(req.birthDate() != null ? req.birthDate() : null);
            patient.setGender(req.gender());
            patient.setBloodType(req.bloodType());
            patient.setEmergencyContactName(req.emergencyContactName());
            patient.setEmergencyContactPhone(req.emergencyContactPhone());
            patient.setEnabled(true);
            return patientRepository.save(patient);
        }

        if (req.role() == Role.LABORATORY_STAFF) {
            Laboratory laboratory = Laboratory.builder()
                    .name(req.labName() != null ? req.labName() : "Not Available")
                    .address(req.labAddress())
                    .phone(req.labPhone())
                    .build();
            Laboratory savedLab = laboratoryRepository.save(laboratory);

            LaboratoryStaff staff = new LaboratoryStaff();
            staff.setFullName(req.fullName() == null ? "Not Available" : req.fullName());
            staff.setEmail(req.email());
            staff.setPassword(passwordEncoder.encode(req.password()));
            staff.setRole(Role.LABORATORY_STAFF);
            staff.setPhone(req.phone());
            staff.setBirthDate(req.birthDate() != null ? req.birthDate() : null);
            staff.setLaboratory(savedLab);
            staff.setEnabled(true);
            return laboratoryStaffRepository.save(staff);
        }

        User u = User.builder()
                .fullName(req.fullName() == null ? "Not Available" : req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .phone(req.phone())
                .birthDate(req.birthDate() != null ? LocalDate.parse(req.birthDate().toString()) : null)
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

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        // ✅ Récupère le patientId si c'est un PATIENT
        Long patientId = null;
        if (user instanceof Patient patient) {
            patientId = patient.getId();
        }

        String token = jwtService.generateToken(userDetails, user.getFullName(), patientId);

        return new AuthResponse(token, userDetails.getUsername(), user.getFullName(), role);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        tokenRepository.deleteByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.isExpired())
            throw new IllegalArgumentException("Token expired");
        if (resetToken.isUsed())
            throw new IllegalArgumentException("Token already used");
        if (newPassword == null || newPassword.length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}