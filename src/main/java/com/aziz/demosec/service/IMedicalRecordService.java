package com.aziz.demosec.service;

import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;

import java.util.List;

public interface IMedicalRecordService {

    MedicalRecordResponse addMedicalRecord(MedicalRecordRequest request);

    MedicalRecordResponse selectMedicalRecordByIdWithGet(Long id);
    MedicalRecordResponse selectMedicalRecordByIdWithOrElse(Long id);

    List<MedicalRecordResponse> selectAllMedicalRecords();

    MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request);

    void deleteMedicalRecordById(Long id);
    void deleteAllMedicalRecords();

    long countingMedicalRecords();
    boolean verifMedicalRecordById(Long id);
}