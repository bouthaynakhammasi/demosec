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
import java.util.HashSet;
import java.util.Set;

@Service
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

    // ================= REGISTER =================
    @Override
    @Transactional
    public User register(RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        Role role = Role.valueOf(req.getRole());

        return switch (role) {
            case PATIENT -> registerPatient(req);
            case DOCTOR -> registerDoctor(req);
            case CLINIC -> registerClinic(req);
            case PHARMACIST -> registerPharmacist(req);
            case LABORATORYSAFF -> registerLabStaff(req);
            case NUTRITIONIST -> registerNutritionist(req);
            case HOME_CARE_PROVIDER -> registerHomeCareProvider(req);
            default -> registerGenericUser(req);
        };
    }

    // ================= PATIENT =================
    private User registerPatient(RegisterRequest req) {
        Patient patient = new Patient();
        mapCommonFields(patient, req);

        try { patient.setGender(Gender.valueOf(req.getGender())); } catch (Exception ignored) {}
        try { patient.setBloodType(BloodType.valueOf(req.getBloodType())); } catch (Exception ignored) {}

        patient.setEmergencyContactName(req.getEmergencyContactName());
        patient.setEmergencyContactPhone(req.getEmergencyContactPhone());
        patient.setProfileCompleted(true);

        return patientRepository.save(patient);
    }

    // ================= DOCTOR =================
    private User registerDoctor(RegisterRequest req) {
        Doctor doctor = new Doctor();
        mapCommonFields(doctor, req);

        doctor.setSpecialty(req.getSpecialty());
        doctor.setLicenseNumber(req.getLicenseNumber() != null ?
                req.getLicenseNumber() : "LIC-" + System.currentTimeMillis());

        doctor.setConsultationFee(req.getConsultationFee() != null ?
                BigDecimal.valueOf(req.getConsultationFee()) : BigDecimal.ZERO);

        try {
            doctor.setConsultationMode(
                    req.getConsultationMode() != null ?
                            ConsultationMode.valueOf(req.getConsultationMode()) :
                            ConsultationMode.BOTH
            );
        } catch (Exception e) {
            doctor.setConsultationMode(ConsultationMode.BOTH);
        }

        if (req.getClinicId() != null) {
            clinicRepository.findById(req.getClinicId()).ifPresent(doctor::setClinic);
        }

        doctor.setProfileCompleted(true);

        return doctorRepository.save(doctor);
    }

    // ================= CLINIC =================
    private User registerClinic(RegisterRequest req) {
        User user = registerGenericUser(req);
        user.setProfileCompleted(true);
        user = userRepository.save(user);

        Clinic clinic = new Clinic();
        clinic.setName(req.getClinicName());
        clinic.setAddress(req.getClinicAddress());
        clinic.setPhone(req.getClinicPhone());
        clinic.setEmergencyPhone(req.getEmergencyPhone());
        clinic.setAmbulancePhone(req.getAmbulancePhone());

        clinicRepository.save(clinic);

        return user;
    }

    // ================= PHARMACIST =================
    private User registerPharmacist(RegisterRequest req) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName(req.getPharmacyName());
        pharmacy.setAddress(req.getPharmacyAddress());
        pharmacy.setPhoneNumber(req.getPharmacyPhone());
        pharmacy.setEmail(req.getPharmacyEmail());

        pharmacy = pharmacyRepository.save(pharmacy);

        Pharmacist pharmacist = new Pharmacist();
        mapCommonFields(pharmacist, req);
        pharmacist.setPharmacy(pharmacy);
        pharmacist.setProfileCompleted(true);

        return pharmacistRepository.save(pharmacist);
    }

    // ================= LAB STAFF =================
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

    // ================= NUTRITIONIST =================
    private User registerNutritionist(RegisterRequest req) {
        Nutritionist nutr = new Nutritionist();
        mapCommonFields(nutr, req);

        nutr.setSpecialties(req.getSpecialty());
        nutr.setLicenseNumber(req.getLicenseNumber());
        nutr.setProfileCompleted(true);

        return nutritionistRepository.save(nutr);
    }

    // ================= HOME CARE =================
    private User registerHomeCareProvider(RegisterRequest req) {
        User user = registerGenericUser(req);
        user.setProfileCompleted(true);
        user = userRepository.save(user);

        ServiceProvider provider = new ServiceProvider();
        provider.setUser(user);
        provider.setCertificationDocument(req.getCertificationDocument());

        if (req.getHomeCareServices() != null) {
            Set<HomeCareService> services = new HashSet<>();
            for (String s : req.getHomeCareServices()) {
                homeCareServiceRepository.findByName(s).ifPresent(services::add);
            }
            provider.setSpecialties(services);
        }

        serviceProviderRepository.save(provider);
        return user;
    }

    // ================= GENERIC =================
    private User registerGenericUser(RegisterRequest req) {
        User user = new User();
        mapCommonFields(user, req);
        return userRepository.save(user);
    }

    // ================= COMMON =================
    private void mapCommonFields(User user, RegisterRequest req) {
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        try {
            user.setRole(Role.valueOf(req.getRole()));
        } catch (Exception e) {
            user.setRole(Role.VISITOR);
        }

        user.setPhone(req.getPhone());
        user.setBirthDate(req.getBirthDate());
        user.setProfileImage(req.getProfileImage());
        user.setEnabled(true);
    }

    // ================= LOGIN =================
    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        User user = userRepository.findByEmail(req.email()).orElseThrow();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_VISITOR");

        String token = jwtService.generateToken(userDetails, user.getFullName(), user.getId());

        return new AuthResponse(token, user.getEmail(), user.getFullName(), role);
    }
}