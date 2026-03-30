package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CompleteProfileRequest;
import com.aziz.demosec.dto.CompleteProfileResponse;
import com.aziz.demosec.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final NutritionistRepository nutritionistRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final HomeCareServiceRepository homeCareServiceRepository;

    @Override
    public CompleteProfileResponse getProfileStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new CompleteProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.isProfileCompleted()
        );
    }

    @Override
    @Transactional
    public CompleteProfileResponse completeProfile(String email, CompleteProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Role role = user.getRole();
        if (role == null) throw new IllegalArgumentException("User role not found");

        switch (role) {
            case DOCTOR -> {
                Doctor doctor = doctorRepository.findById(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

                if (request.specialty() != null) doctor.setSpecialty(request.specialty());
                if (request.licenseNumber() != null) doctor.setLicenseNumber(request.licenseNumber());
                if (request.yearsOfExperience() != null) doctor.setYearsOfExperience(request.yearsOfExperience());
                if (request.consultationFee() != null) doctor.setConsultationFee(request.consultationFee());
                if (request.consultationMode() != null) doctor.setConsultationMode(request.consultationMode());
                
                doctorRepository.save(doctor);
            }

            case NUTRITIONIST -> {
                Nutritionist n = nutritionistRepository.findById(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Nutritionist not found"));

                if (request.licenseNumber() != null) n.setLicenseNumber(request.licenseNumber());
                if (request.specialties() != null) n.setSpecialties(request.specialties());
                if (request.yearsOfExperience() != null) n.setYearsOfExperience(request.yearsOfExperience());
                if (request.bio() != null) n.setBio(request.bio());
                
                n.setVerified(false);
                nutritionistRepository.save(n);
            }

            case HOME_CARE_PROVIDER -> {
                ServiceProvider sp = serviceProviderRepository.findById(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Service Provider not found"));

                if (request.certificationDocument() != null) sp.setCertificationDocument(request.certificationDocument());
                if (request.serviceIds() != null && !request.serviceIds().isEmpty()) {
                    List<HomeCareService> services = homeCareServiceRepository.findAllById(request.serviceIds());
                    sp.setSpecialties(new HashSet<>(services));
                }
                sp.setVerified(false);
                serviceProviderRepository.save(sp);
            }

            default -> { }
        }

        user.setProfileCompleted(true);
        User savedUser = userRepository.save(user);

        return new CompleteProfileResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.isProfileCompleted()
        );
    }
}