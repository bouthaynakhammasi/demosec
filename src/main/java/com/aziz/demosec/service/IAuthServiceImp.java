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

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
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
    private final MedicalRecordRepository medicalRecordRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public IAuthServiceImp(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            ClinicRepository clinicRepository,
            PharmacyRepository pharmacyRepository,
            PharmacistRepository pharmacistRepository,
            LaboratoryRepository laboratoryRepository,
            LaboratoryStaffRepository laboratoryStaffRepository,
            NutritionistRepository nutritionistRepository,
            ServiceProviderRepository serviceProviderRepository,
            HomeCareServiceRepository homeCareServiceRepository,
            MedicalRecordRepository medicalRecordRepository,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.laboratoryStaffRepository = laboratoryStaffRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.homeCareServiceRepository = homeCareServiceRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public User register(RegisterRequest req) {
        log.info("Processing registration for email: {} with role: {}", req.email(), req.role());

        if (req.email() == null || req.email().isBlank())
            throw new IllegalArgumentException("Email required");
        if (req.password() == null || req.password().length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        if (req.role() == null)
            throw new IllegalArgumentException("Role required");
        if (userRepository.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException("Email already used");

        User u;
        switch (req.role()) {
            case PATIENT:
                Patient patient = new Patient();
                fillBasicUserInfo(patient, req);
                patient.setGender(req.gender());
                patient.setBloodType(req.bloodType());
                patient.setEmergencyContactName(req.emergencyContactName());
                patient.setEmergencyContactPhone(req.emergencyContactPhone());
                patient.setGlucoseRate(req.glucoseRate());
                patient.setAllergies(req.allergies());
                patient.setDiseases(req.diseases());
                patient.setHeight(req.height());
                patient.setWeight(req.weight());
                u = patientRepository.save(patient);
                
                // Create Medical Record automatically for Patients
                MedicalRecord record = MedicalRecord.builder().patient(u).build();
                medicalRecordRepository.save(record);
                break;

            case DOCTOR:
                Doctor dr = new Doctor();
                fillBasicUserInfo(dr, req);
                dr.setSpecialty(req.specialty());
                dr.setLicenseNumber(req.licenseNumber());
                dr.setConsultationFee(req.consultationFee());
                dr.setConsultationMode(req.consultationMode());
                u = doctorRepository.save(dr);
                break;

            case CLINIC:
                Clinic cEntity = new Clinic();
                fillBasicUserInfo(cEntity, req);
                cEntity.setName(req.clinicName());
                cEntity.setAddress(req.clinicAddress());
                cEntity.setPhone(req.clinicPhone());
                cEntity.setEmergencyPhone(req.emergencyPhone());
                cEntity.setAmbulancePhone(req.ambulancePhone());
                u = clinicRepository.save(cEntity);
                break;

            case PHARMACIST:
                Pharmacist pharm = new Pharmacist();
                fillBasicUserInfo(pharm, req);
                Pharmacy phEntity = new Pharmacy();
                phEntity.setName(req.pharmacyName());
                phEntity.setAddress(req.pharmacyAddress());
                phEntity.setPhoneNumber(req.pharmacyPhone());
                phEntity.setEmail(req.pharmacyEmail());
                phEntity = pharmacyRepository.save(phEntity);
                pharm.setPharmacy(phEntity);
                u = pharmacistRepository.save(pharm);
                break;

            case LABORATORYSTAFF:
                LaboratoryStaff labStaff = new LaboratoryStaff();
                fillBasicUserInfo(labStaff, req);
                Laboratory labEntity = new Laboratory();
                labEntity.setName(req.labName());
                labEntity.setAddress(req.labAddress());
                labEntity.setPhone(req.labPhone());
                labEntity = laboratoryRepository.save(labEntity);
                labStaff.setLaboratory(labEntity);
                u = laboratoryStaffRepository.save(labStaff);
                break;

            case NUTRITIONIST:
                Nutritionist nut = new Nutritionist();
                fillBasicUserInfo(nut, req);
                nut.setSpecialties(req.specialty());
                nut.setConsultationFee(req.consultationFee());
                nut.setLicenseNumber(req.licenseNumber());
                nut.setConsultationMode(req.consultationMode());
                u = nutritionistRepository.save(nut);
                break;

            case HOME_CARE_PROVIDER:
                ServiceProvider sp = new ServiceProvider();
                fillBasicUserInfo(sp, req);
                sp.setCertificationDocument(req.certificationDocument());
                if (req.homeCareServices() != null && !req.homeCareServices().isEmpty()) {
                    Set<HomeCareService> services = new HashSet<>();
                    for (String serviceName : req.homeCareServices()) {
                        homeCareServiceRepository.findByName(serviceName).ifPresent(services::add);
                    }
                    sp.setSpecialties(services);
                }
                u = serviceProviderRepository.save(sp);
                break;

            default:
                u = new User();
                fillBasicUserInfo(u, req);
                u = userRepository.save(u);
                break;
        }

        return u;
    }

    private void fillBasicUserInfo(User u, RegisterRequest req) {
        u.setFullName(req.fullName() == null ? "Not Available" : req.fullName());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(req.role());
        u.setPhone(req.phone());
        u.setBirthDate(req.birthDate());
        u.setEnabled(true);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        String token = jwtService.generateToken(userDetails, user.getFullName(), user.getId());

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