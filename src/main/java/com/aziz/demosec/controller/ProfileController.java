package com.aziz.demosec.controller;


import com.aziz.demosec.dto.CompleteProfileRequest;
import com.aziz.demosec.dto.CompleteProfileResponse;
import com.aziz.demosec.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<CompleteProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getProfileStatus(email));
    }

    @PatchMapping("/complete")
    public ResponseEntity<CompleteProfileResponse> completeProfile(
            Authentication authentication,
            @RequestBody CompleteProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.completeProfile(email, request));
    }
}
