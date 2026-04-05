package com.aziz.demosec.service;

import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;

import java.util.List;

public interface IPrescriptionService {

    PrescriptionResponse addPrescription(PrescriptionRequest request);

    PrescriptionResponse selectPrescriptionByIdWithGet(Long id);
    PrescriptionResponse selectPrescriptionByIdWithOrElse(Long id);

    List<PrescriptionResponse> selectAllPrescriptions();

    PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request);

    void deletePrescriptionById(Long id);
    void deleteAllPrescriptions();

    long countingPrescriptions();
    boolean verifPrescriptionById(Long id);
}