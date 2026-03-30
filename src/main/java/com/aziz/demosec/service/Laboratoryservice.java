package com.aziz.demosec.service;

import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;

import java.util.List;

public interface LaboratoryService {

    LaboratoryResponse create(LaboratoryRequest request);

    LaboratoryResponse getById(Long id);

    List<LaboratoryResponse> getAll();

    List<LaboratoryResponse> searchByName(String name);

    List<LaboratoryResponse> getActive();

    LaboratoryResponse update(Long id, LaboratoryRequest request);

    LaboratoryResponse toggleActive(Long id);

    void delete(Long id);

    LaboratoryResponse getMyLaboratory(String email);

    LaboratoryResponse updateProfile(String email, LaboratoryStaffProfileUpdateRequest request);
}
