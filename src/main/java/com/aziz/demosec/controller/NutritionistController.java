package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.ConsultationMode;
import com.aziz.demosec.Entities.Nutritionist;
import com.aziz.demosec.dto.NutritionistProfileResponse;
import com.aziz.demosec.dto.NutritionistProfileUpdateRequest;
import com.aziz.demosec.repository.NutritionistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/nutritionists")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NutritionistController {

    private final NutritionistRepository nutritionistRepository;

    @GetMapping("/me")
    public ResponseEntity<NutritionistProfileResponse> getMe(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return nutritionistRepository.findByEmail(principal.getName())
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<NutritionistProfileResponse> updateProfile(Principal principal, @RequestBody NutritionistProfileUpdateRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return nutritionistRepository.findByEmail(principal.getName())
                .map(nutritionist -> {
                    if (request.fullName() != null) nutritionist.setFullName(request.fullName());
                    if (request.phone() != null) nutritionist.setPhone(request.phone());
                    if (request.birthDate() != null) nutritionist.setBirthDate(request.birthDate());
                    if (request.photo() != null) nutritionist.setPhoto(request.photo());
                    if (request.specialties() != null) nutritionist.setSpecialties(request.specialties());
                    if (request.consultationFee() != null) nutritionist.setConsultationFee(request.consultationFee());
                    if (request.consultationMode() != null) nutritionist.setConsultationMode(ConsultationMode.valueOf(request.consultationMode()));
                    
                    Nutritionist saved = nutritionistRepository.save(nutritionist);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private NutritionistProfileResponse mapToResponse(Nutritionist nutritionist) {
        return new NutritionistProfileResponse(
                nutritionist.getId(),
                nutritionist.getFullName(),
                nutritionist.getEmail(),
                nutritionist.getPhone(),
                nutritionist.getBirthDate(),
                nutritionist.getPhoto(),
                nutritionist.getLicenseNumber(),
                nutritionist.getSpecialties(),
                nutritionist.getConsultationFee(),
                nutritionist.getConsultationMode() != null ? nutritionist.getConsultationMode().name() : null
        );
    }
}
