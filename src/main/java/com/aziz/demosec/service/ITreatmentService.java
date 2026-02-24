package com.aziz.demosec.service;

import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;

import java.util.List;

public interface ITreatmentService {

    TreatmentResponse addTreatment(TreatmentRequest request);

    TreatmentResponse selectTreatmentByIdWithGet(Long id);
    TreatmentResponse selectTreatmentByIdWithOrElse(Long id);

    List<TreatmentResponse> selectAllTreatments();

    TreatmentResponse updateTreatment(Long id, TreatmentRequest request);

    void deleteTreatmentById(Long id);
    void deleteAllTreatments();

    long countingTreatments();
    boolean verifTreatmentById(Long id);
}