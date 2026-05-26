package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.Pharmacist;
import com.aziz.demosec.Entities.Pharmacy;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

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
    private final INotificationService notificationService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public User register(RegisterRequest req, String documentUrl) {
        log.info("Registering user: {} with role: {}", req.getEmail(), req.getRole());

        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("Email required");

        if (req.getPassword() == null || req.getPassword().length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters");

        if (req.getRole() == null)
            throw new IllegalArgumentException("Role required");

        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already used");

        if (req.getBirthDate() != null) {
            com.aziz.demosec.util.BirthDateValidator.validate(req.getBirthDate());
        }

        Role role = req.getRole();
        User user;
        switch (role) {
            case PATIENT -> user = registerPatient(req);
            case DOCTOR -> user = registerDoctor(req);
            case CLINIC -> user = registerClinic(req);
            case PHARMACIST -> user = registerPharmacist(req, documentUrl);
            case LABORATORY_STAFF -> user = registerLabStaff(req);
            case NUTRITIONIST -> user = registerNutritionist(req);
            case HOME_CARE_PROVIDER -> user = registerHomeCareProvider(req, documentUrl);
            default -> {
                user = new User();
                mapCommonFields(user, req);
                user = userRepository.save(user);
            }
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
        
        patient.setChronicDiseases(req.getChronicDiseases());
        patient.setDrugAllergies(req.getDrugAllergies());
        patient.setHereditaryDiseases(req.getHereditaryDiseases());
        
        patient.setProfileCompleted(true);
        patient.setEnabled(true);
        return patientRepository.save(patient);
    }

    private User registerDoctor(RegisterRequest req) {
        com.aziz.demosec.Entities.Doctor doctor = new com.aziz.demosec.Entities.Doctor();
        mapCommonFields(doctor, req);
        doctor.setSpecialty(req.getSpecialty());
        doctor.setLicenseNumber(req.getLicenseNumber() != null ? req.getLicenseNumber() : "PENDING");
        doctor.setConsultationFee(req.getConsultationFee() != null ? req.getConsultationFee() : BigDecimal.ZERO);
        doctor.setConsultationMode(req.getConsultationMode() != null ? req.getConsultationMode() : com.aziz.demosec.Entities.ConsultationMode.BOTH);
        if (req.getClinicId() != null) {
            clinicRepository.findById(req.getClinicId()).ifPresent(doctor::setClinic);
        }
        doctor.setProfileCompleted(true);
        doctor.setEnabled(true);
        return doctorRepository.save(doctor);
    }

    private User registerClinic(RegisterRequest req) {
        com.aziz.demosec.Entities.Clinic clinic = new com.aziz.demosec.Entities.Clinic();
        mapCommonFields(clinic, req);
        clinic.setName(req.getClinicName());
        clinic.setAddress(req.getClinicAddress());
        clinic.setPhone(req.getClinicPhone());
        clinic.setEmergencyPhone(req.getEmergencyPhone());
        clinic.setAmbulancePhone(req.getAmbulancePhone());
        clinic.setProfileCompleted(true);
        clinic.setEnabled(true);
        return clinicRepository.save(clinic);
    }

    private User registerPharmacist(RegisterRequest req, String documentUrl) {
        Pharmacy ph = Pharmacy.builder()
                .name(req.getPharmacyName())
                .address(req.getPharmacyAddress() != null ? req.getPharmacyAddress() : "")
                .phoneNumber(req.getPharmacyPhone() != null ? req.getPharmacyPhone() : "")
                .email(req.getEmail())
                .build();
        ph = pharmacyRepository.save(ph);

        Pharmacist pharmacist = new Pharmacist();
        mapCommonFields(pharmacist, req);
        pharmacist.setPharmacy(ph);
        pharmacist.setProfessionalDocument(documentUrl);
        pharmacist.setProfileCompleted(true);
        pharmacist.setEnabled(false); // Disabled until admin approval

        Pharmacist saved = pharmacistRepository.save(pharmacist);
        notificationService.notifyAdminsOfNewPharmacist(saved);
        return saved;
    }

    private User registerLabStaff(RegisterRequest req) {
        com.aziz.demosec.Entities.Laboratory lab = new com.aziz.demosec.Entities.Laboratory();
        lab.setName(req.getLabName());
        lab.setAddress(req.getLabAddress());
        lab.setPhone(req.getLabPhone());
        lab = laboratoryRepository.save(lab);

        com.aziz.demosec.Entities.LaboratoryStaff staff = new com.aziz.demosec.Entities.LaboratoryStaff();
        mapCommonFields(staff, req);
        staff.setLaboratory(lab);
        staff.setProfileCompleted(true);
        staff.setEnabled(true);
        return laboratoryStaffRepository.save(staff);
    }

    private User registerNutritionist(RegisterRequest req) {
        com.aziz.demosec.Entities.Nutritionist nut = new com.aziz.demosec.Entities.Nutritionist();
        mapCommonFields(nut, req);
        nut.setSpecialties(req.getSpecialty());
        nut.setConsultationFee(req.getConsultationFee() != null ? req.getConsultationFee() : BigDecimal.ZERO);
        nut.setLicenseNumber(req.getLicenseNumber());
        nut.setConsultationMode(req.getConsultationMode());
        nut.setProfileCompleted(true);
        nut.setEnabled(true);
        return nutritionistRepository.save(nut);
    }

    private User registerHomeCareProvider(RegisterRequest req, String documentUrl) {
        com.aziz.demosec.Entities.ServiceProvider provider = new com.aziz.demosec.Entities.ServiceProvider();
        mapCommonFields(provider, req);
        provider.setCertificationDocument(documentUrl);
        provider.setVerified(false);
        provider.setEnabled(false); // Disabled until admin approval

        if (req.getSpecialtyIds() != null && !req.getSpecialtyIds().isEmpty()) {
            java.util.List<com.aziz.demosec.Entities.HomeCareService> services = homeCareServiceRepository.findAllById(req.getSpecialtyIds());
            provider.setSpecialties(new java.util.HashSet<>(services));
        }
        
        provider.setProfileCompleted(true);
        com.aziz.demosec.Entities.ServiceProvider saved = serviceProviderRepository.save(provider);
        notificationService.notifyAdminsOfNewProvider(saved);
        return saved;
    }

    private void mapCommonFields(User user, RegisterRequest req) {
        user.setFullName(req.getFullName() == null ? "Not Available" : req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setPhone(req.getPhone());
        user.setBirthDate(req.getBirthDate());
        user.setProfileImage(req.getProfileImage());
        user.setEnabled(true);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        log.info("Login attempt for email: [{}]", req.email());

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
        if (user instanceof com.aziz.demosec.Entities.LaboratoryStaff staff) {
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