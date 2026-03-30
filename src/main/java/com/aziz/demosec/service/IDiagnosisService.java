package com.aziz.demosec.service;

import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;

import java.util.List;

public interface IDiagnosisService {

    DiagnosisResponse addDiagnosis(DiagnosisRequest request);

    DiagnosisResponse selectDiagnosisByIdWithGet(Long id);
    DiagnosisResponse selectDiagnosisByIdWithOrElse(Long id);

    List<DiagnosisResponse> selectAllDiagnoses();

    DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request);

    void deleteDiagnosisById(Long id);
    void deleteAllDiagnoses();

    long countingDiagnoses();
    boolean verifDiagnosisById(Long id);
}