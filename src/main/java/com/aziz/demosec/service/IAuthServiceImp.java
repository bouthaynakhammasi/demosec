package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.PasswordResetToken;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//@Service
@RequiredArgsConstructor
@Slf4j
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PharmacistRepository pharmacistRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryStaffRepository laboratoryStaffRepository;
    private final NutritionistRepository nutritionistRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final HomeCareServiceRepository homeCareServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public User register(RegisterRequest req, String documentUrl) {
        log.info("Registering user: {} with role: {}", req.getEmail(), req.getRole());

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        Role role = req.getRole();
        if (role == null) throw new IllegalArgumentException("Role required");

        User user;
        switch (role) {
            case PATIENT -> user = registerPatient(req);
            case ADMIN -> user = registerGenericUser(req);
            case DOCTOR -> user = registerDoctor(req);
            case CLINIC -> user = registerClinic(req);
            case PHARMACIST -> user = registerPharmacist(req);
            case LABORATORY_STAFF -> user = registerLabStaff(req);
            case NUTRITIONIST -> user = registerNutritionist(req);
            case HOME_CARE_PROVIDER -> user = registerHomeCareProvider(req);
            default -> user = registerGenericUser(req);
        }
        return user;
    }

    private User registerPatient(RegisterRequest req) {
        Patient patient = new Patient();
        mapCommonFields(patient, req);
        patient.setGender(req.getGender());
        patient.setBloodType(req.getBloodType());
        patient.setEmergencyContactName(req.getEmergencyContactName());
        patient.setEmergencyContactPhone(req.getEmergencyContactPhone());
        patient.setGlucoseRate(req.getGlucoseRate());
        patient.setAllergies(req.getAllergies());
        patient.setDiseases(req.getDiseases());
        patient.setHeight(req.getHeight());
        patient.setWeight(req.getWeight());
        patient.setProfileCompleted(true);
        return patientRepository.save(patient);
    }

    private User registerDoctor(RegisterRequest req) {
        Doctor doctor = new Doctor();
        mapCommonFields(doctor, req);
        doctor.setSpecialty(req.getSpecialty());
        doctor.setLicenseNumber(req.getLicenseNumber() != null ? req.getLicenseNumber() : "PENDING");
        doctor.setConsultationFee(req.getConsultationFee() != null ? req.getConsultationFee() : BigDecimal.ZERO);
        doctor.setConsultationMode(req.getConsultationMode() != null ? req.getConsultationMode() : ConsultationMode.BOTH);
        if (req.getClinicId() != null) {
            clinicRepository.findById(req.getClinicId()).ifPresent(doctor::setClinic);
        }
        doctor.setProfileCompleted(true);
        return doctorRepository.save(doctor);
    }

    private User registerClinic(RegisterRequest req) {
        Clinic clinic = new Clinic();
        mapCommonFields(clinic, req);
        clinic.setName(req.getClinicName());
        clinic.setAddress(req.getClinicAddress());
        clinic.setPhone(req.getClinicPhone());
        clinic.setEmergencyPhone(req.getEmergencyPhone());
        clinic.setAmbulancePhone(req.getAmbulancePhone());
        clinic.setProfileCompleted(true);
        return clinicRepository.save(clinic);
    }

    private User registerPharmacist(RegisterRequest req) {
        Pharmacy ph = new Pharmacy();
        ph.setName(req.getPharmacyName());
        ph.setAddress(req.getPharmacyAddress());
        ph.setPhoneNumber(req.getPharmacyPhone());
        ph.setEmail(req.getPharmacyEmail());
        ph = pharmacyRepository.save(ph);

        Pharmacist pharmacist = new Pharmacist();
        mapCommonFields(pharmacist, req);
        pharmacist.setPharmacy(ph);
        pharmacist.setProfileCompleted(true);
        return pharmacistRepository.save(pharmacist);
    }

    private User registerLabStaff(RegisterRequest req) {
        Laboratory lab = new Laboratory();
        lab.setName(req.getLabName());
        lab.setAddress(req.getLabAddress());
        lab.setPhone(req.getLabPhone());
        lab = laboratoryRepository.save(lab);

        LaboratoryStaff staff = new LaboratoryStaff();
        mapCommonFields(staff, req);
        staff.setLaboratory(lab);
        staff.setProfileCompleted(true);
        return laboratoryStaffRepository.save(staff);
    }

    private User registerNutritionist(RegisterRequest req) {
        Nutritionist nut = new Nutritionist();
        mapCommonFields(nut, req);
        nut.setSpecialties(req.getSpecialty());
        nut.setConsultationFee(req.getConsultationFee() != null ? req.getConsultationFee() : BigDecimal.ZERO);
        nut.setLicenseNumber(req.getLicenseNumber());
        nut.setConsultationMode(req.getConsultationMode());
        nut.setProfileCompleted(true);
        return nutritionistRepository.save(nut);
    }

    private User registerHomeCareProvider(RegisterRequest req) {
        ServiceProvider sp = new ServiceProvider();
        mapCommonFields(sp, req);
        sp.setCertificationDocument(req.getCertificationDocument());
        if (req.getHomeCareServices() != null) {
            Set<HomeCareService> services = new HashSet<>();
            for (String name : req.getHomeCareServices()) {
                homeCareServiceRepository.findByName(name).ifPresent(services::add);
            }
            sp.setSpecialties(services);
        }
        sp.setProfileCompleted(true);
        return serviceProviderRepository.save(sp);
    }

    private User registerGenericUser(RegisterRequest req) {
        User user = new User();
        mapCommonFields(user, req);
        return userRepository.save(user);
    }

    private void mapCommonFields(User user, RegisterRequest req) {
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setPhone(req.getPhone());
        if (req.getBirthDate() != null) {
            user.setBirthDate(req.getBirthDate());
        }
        user.setProfileImage(req.getProfileImage());
        user.setEnabled(true);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        User user = userRepository.findByEmail(req.email()).orElseThrow();

        String roleStr = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        Long laboratoryId = null;
        if (user instanceof LaboratoryStaff staff) {
            laboratoryId = staff.getLaboratory() != null ? staff.getLaboratory().getId() : null;
        }

        String gender = null;
        if (user instanceof Patient p) gender = p.getGender() != null ? p.getGender().name() : null;

        String token = jwtService.generateToken(
                userDetails,
                user.getFullName(),
                user.getId(),
                gender,
                laboratoryId
        );

        return new AuthResponse(token, user.getEmail(), user.getFullName(), roleStr);
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

        if (resetToken.isExpired()) throw new IllegalArgumentException("Token expired");
        if (resetToken.isUsed()) throw new IllegalArgumentException("Token already used");
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}