package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final MedicalHistoryRepository medicalHistoryRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

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
            MedicalHistoryRepository medicalHistoryRepository,
            @org.springframework.context.annotation.Lazy PasswordEncoder passwordEncoder,
            @org.springframework.context.annotation.Lazy AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
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
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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
            patient.setGlucoseRate(req.glucoseRate());
            patient.setAllergies(req.allergies());
            patient.setDiseases(req.diseases());
            patient.setHeight(req.height());
            patient.setWeight(req.weight());
            patient.setEnabled(true);

            patient = patientRepository.save(patient);

            // Create Medical Record automatically for Patients
            MedicalRecord record = MedicalRecord.builder().patient(patient).build();
            medicalRecordRepository.save(record);

            return patient;
        }

        // =========================
        // OTHER ROLES
        // =========================
        User u;
        switch (req.role()) {
            case DOCTOR:
                Doctor dr = new Doctor();
                dr.setSpecialty(req.specialty());
                dr.setLicenseNumber(req.licenseNumber());
                dr.setConsultationFee(req.consultationFee());
                dr.setConsultationMode(req.consultationMode());
                u = dr;
                break;
            case CLINIC:
                Clinic cEntity = new Clinic();
                cEntity.setName(req.clinicName());
                cEntity.setAddress(req.clinicAddress());
                cEntity.setPhone(req.clinicPhone());
                cEntity.setEmergencyPhone(req.emergencyPhone());
                cEntity.setAmbulancePhone(req.ambulancePhone());
                u = cEntity;
                break;
            case PHARMACIST:
                Pharmacist pharm = new Pharmacist();
                Pharmacy phEntity = new Pharmacy();
                phEntity.setName(req.pharmacyName());
                phEntity.setAddress(req.pharmacyAddress());
                phEntity.setPhoneNumber(req.pharmacyPhone());
                phEntity.setEmail(req.pharmacyEmail());
                phEntity = pharmacyRepository.save(phEntity);
                pharm.setPharmacy(phEntity);
                u = pharm;
                break;
            case LABORATORYSTAFF:
                LaboratoryStaff labStaff = new LaboratoryStaff();
                Laboratory labEntity = new Laboratory();
                labEntity.setName(req.labName());
                labEntity.setAddress(req.labAddress());
                labEntity.setPhone(req.labPhone());
                labEntity = laboratoryRepository.save(labEntity);
                labStaff.setLaboratory(labEntity);
                u = labStaff;
                break;
            case NUTRITIONIST:
                Nutritionist nut = new Nutritionist();
                nut.setSpecialties(req.specialty());
                nut.setConsultationFee(req.consultationFee());
                nut.setLicenseNumber(req.licenseNumber());
                nut.setConsultationMode(req.consultationMode());
                u = nut;
                break;
            case HOME_CARE_PROVIDER:
                ServiceProvider sp = new ServiceProvider();
                sp.setCertificationDocument(req.certificationDocument());
                if (req.homeCareServices() != null && !req.homeCareServices().isEmpty()) {
                    Set<HomeCareService> services = new HashSet<>();
                    for (String serviceName : req.homeCareServices()) {
                        homeCareServiceRepository.findByName(serviceName).ifPresent(services::add);
                    }
                    sp.setSpecialties(services);
                }
                u = sp;
                break;
            default:
                u = new User();
                break;
        }

        u.setFullName(req.fullName() == null ? "Not Available" : req.fullName());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(req.role());

        if (req.role() != Role.CLINIC) {
            u.setPhone(req.phone());
        }

        u.setBirthDate(req.birthDate());
        u.setEnabled(true);

        if (u instanceof Doctor) return doctorRepository.save((Doctor) u);
        if (u instanceof Clinic) return clinicRepository.save((Clinic) u);
        if (u instanceof Pharmacist) return pharmacistRepository.save((Pharmacist) u);
        if (u instanceof LaboratoryStaff) return laboratoryStaffRepository.save((LaboratoryStaff) u);
        if (u instanceof Nutritionist) return nutritionistRepository.save((Nutritionist) u);

        return userRepository.save(u);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new RuntimeException("User not found after authentication"));
        
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        String token = jwtService.generateToken(userDetails, user.getFullName(), user.getId());
        return new AuthResponse(token, userDetails.getUsername(), user.getFullName(), role);
    }
}
