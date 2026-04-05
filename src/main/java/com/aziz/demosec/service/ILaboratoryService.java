package com.aziz.demosec.service;

import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;

public interface ILaboratoryService {
    LaboratoryResponse getLaboratoryForCurrentUser(String email);
    LaboratoryResponse updateProfile(String email, LaboratoryStaffProfileUpdateRequest request);
}
