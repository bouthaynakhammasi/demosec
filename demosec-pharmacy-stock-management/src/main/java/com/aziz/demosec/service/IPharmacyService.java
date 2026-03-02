package com.aziz.demosec.service;

import com.aziz.demosec.dto.request.PharmacyRequest;
import com.aziz.demosec.dto.response.PharmacyResponse;

import java.util.List;

public interface IPharmacyService {
    PharmacyResponse create(PharmacyRequest request);
    PharmacyResponse update(Long id, PharmacyRequest request);
    PharmacyResponse getById(Long id);
    List<PharmacyResponse> getAll();
    void delete(Long id);
}