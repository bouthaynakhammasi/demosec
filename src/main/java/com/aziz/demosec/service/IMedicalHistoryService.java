package com.aziz.demosec.service;

import com.aziz.demosec.dto.MedicalHistoryRequest;
import com.aziz.demosec.dto.MedicalHistoryResponse;

import java.util.List;

public interface IMedicalHistoryService {

    MedicalHistoryResponse addMedicalHistory(MedicalHistoryRequest request);

    MedicalHistoryResponse selectMedicalHistoryByIdWithGet(Long id);
    MedicalHistoryResponse selectMedicalHistoryByIdWithOrElse(Long id);

    List<MedicalHistoryResponse> selectAllMedicalHistories();

    MedicalHistoryResponse updateMedicalHistory(Long id, MedicalHistoryRequest request);

    void deleteMedicalHistoryById(Long id);
    void deleteAllMedicalHistories();

    long countingMedicalHistories();
    boolean verifMedicalHistoryById(Long id);
}