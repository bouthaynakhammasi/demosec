package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.dto.DoctorProfileDTO;
import com.aziz.demosec.repository.ClinicRepository;
import com.aziz.demosec.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final com.aziz.demosec.repository.ReviewRepository reviewRepository;
    private final com.aziz.demosec.repository.UserRepository userRepository;
    private final com.aziz.demosec.repository.AppointmentRepository appointmentRepository;

    @GetMapping("/{id}/profile")
    public ResponseEntity<DoctorProfileDTO> getProfile(@PathVariable("id") Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        return ResponseEntity.ok(mapToDTO(doctor));
    }

    @PutMapping("/{id}/profile")
    @Transactional
    public ResponseEntity<DoctorProfileDTO> updateProfile(@PathVariable("id") Long id, @RequestBody DoctorProfileDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        doctor.setSpecialty(dto.getSpecialty());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setConsultationMode(dto.getConsultationMode());
        doctor.setClinicAddress(dto.getClinicAddress());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        
        if (dto.getProfilePicture() != null) {
            doctor.setProfilePicture(dto.getProfilePicture());
        }
        
        if (dto.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(dto.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            doctor.setClinic(clinic);
        } else {
            doctor.setClinic(null);
        }

        if (dto.getLicenseNumber() != null && !dto.getLicenseNumber().isEmpty()) {
            doctor.setLicenseNumber(dto.getLicenseNumber());
        }

        doctorRepository.save(doctor);
        
        return getProfile(id);
    }

    @GetMapping
    public ResponseEntity<java.util.List<DoctorProfileDTO>> getAllDoctors() {
        java.util.List<DoctorProfileDTO> doctors = doctorRepository.findAll().stream()
                .filter(d -> d.isEnabled() && d.getSpecialty() != null)
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> getDoctorDetails(@PathVariable("id") Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        return ResponseEntity.ok(mapToDTO(doctor));
    }

    private DoctorProfileDTO mapToDTO(Doctor doctor) {
        boolean isProfileComplete = doctor.getSpecialty() != null && doctor.getConsultationMode() != null;
        // Dynamically calculate patient count and rating
        long patientCount = appointmentRepository.findByDoctorId(doctor.getId()).stream()
                .map(a -> a.getPatient().getId())
                .distinct()
                .count();
        
        java.util.List<com.aziz.demosec.Entities.Review> allReviews = reviewRepository.findByDoctorId(doctor.getId());
        double avg = allReviews.stream().mapToInt(com.aziz.demosec.Entities.Review::getRating).average().orElse(0.0);
        double finalRating = Math.round(avg * 10.0) / 10.0;

        return DoctorProfileDTO.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .specialty(doctor.getSpecialty())
                .licenseNumber(doctor.getLicenseNumber() != null ? doctor.getLicenseNumber() : "PENDING")
                .yearsOfExperience(doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() : 2) // Fallback for UX
                .consultationFee(doctor.getConsultationFee())
                .consultationMode(doctor.getConsultationMode())
                .clinicAddress(doctor.getClinicAddress())
                .clinicId(doctor.getClinic() != null ? doctor.getClinic().getId() : null)
                .clinicName(doctor.getClinic() != null ? doctor.getClinic().getName() : null)
                .isProfileComplete(isProfileComplete)
                .bio(doctor.getBio())
                .patientCount((int) patientCount)
                .rating(finalRating)
                .profilePicture(doctor.getProfilePicture())
                .build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<java.util.List<com.aziz.demosec.dto.ReviewDTO>> getDoctorReviews(@PathVariable("id") Long id) {
        java.util.List<com.aziz.demosec.dto.ReviewDTO> reviews = reviewRepository.findByDoctorId(id).stream()
                .map(r -> com.aziz.demosec.dto.ReviewDTO.builder()
                        .id(r.getId())
                        .doctorId(r.getDoctor().getId())
                        .patientName(anonymizeName(r.getPatient().getFullName()))
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    private String anonymizeName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "A***";
        String[] parts = fullName.split(" ");
        StringBuilder anon = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                anon.append(part.charAt(0)).append("*** ");
            }
        }
        return anon.toString().trim();
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ResponseEntity<com.aziz.demosec.dto.ReviewDTO> addReview(
            @PathVariable("id") Long id,
            @RequestBody com.aziz.demosec.dto.ReviewRequest request,
            org.springframework.security.core.Authentication authentication) {
        
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Unauthorized");
        }
            
        String email = authentication.getName();
        com.aziz.demosec.domain.User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        com.aziz.demosec.Entities.Review review = com.aziz.demosec.Entities.Review.builder()
                .patient(patient)
                .doctor(doctor)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
                
        review = reviewRepository.save(review);
        
        // Update doctor rating calculation
        java.util.List<com.aziz.demosec.Entities.Review> allReviews = reviewRepository.findByDoctorId(id);
        double avg = allReviews.stream().mapToInt(com.aziz.demosec.Entities.Review::getRating).average().orElse(0.0);
        doctor.setRating(Math.round(avg * 10.0) / 10.0);
        doctorRepository.save(doctor);

        return ResponseEntity.ok(com.aziz.demosec.dto.ReviewDTO.builder()
                .id(review.getId())
                .doctorId(doctor.getId())
                .patientName(anonymizeName(patient.getFullName()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt() : java.time.LocalDateTime.now())
                .build());
    }
}
