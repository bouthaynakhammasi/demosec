package com.aziz.demosec.service;

import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;

import java.util.List;

public interface IConsultationService {

    ConsultationResponse addConsultation(ConsultationRequest request);

    ConsultationResponse selectConsultationByIdWithGet(Long id);
    ConsultationResponse selectConsultationByIdWithOrElse(Long id);

    List<ConsultationResponse> selectAllConsultations();

    ConsultationResponse updateConsultation(Long id, ConsultationRequest request);

    void deleteConsultationById(Long id);
    void deleteAllConsultations();

    long countingConsultations();
    boolean verifConsultationById(Long id);
}