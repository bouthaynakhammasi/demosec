package com.aziz.demosec.service;

import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;

import java.util.List;

public interface Laboratoryservice {
    public interface LaboratoryService {
        LaboratoryResponse create(LaboratoryRequest request);
        LaboratoryResponse getById(Long id);
        List<LaboratoryResponse> getAll();
        LaboratoryResponse update(Long id, LaboratoryRequest request);
        void delete(Long id);
    }

}
