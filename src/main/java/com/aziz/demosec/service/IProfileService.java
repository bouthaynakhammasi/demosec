package com.aziz.demosec.service;

import com.aziz.demosec.dto.CompleteProfileRequest;
import com.aziz.demosec.dto.CompleteProfileResponse;

public interface IProfileService {
    CompleteProfileResponse getProfileStatus(String email);

    CompleteProfileResponse completeProfile(String email, CompleteProfileRequest request);
}