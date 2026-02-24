package com.aziz.demosec.service;

import com.aziz.demosec.Entities.MedicalHistory;
import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.repository.MedicalHistoryRepository;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.dto.MedicalHistoryRequest;
import com.aziz.demosec.dto.MedicalHistoryResponse;
import com.aziz.demosec.Mapper.MedicalHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MedicalHistoryService implements IMedicalHistoryService {

    private MedicalHistoryRepository medicalHistoryRepository;
    private MedicalRecordRepository medicalRecordRepository;
    private MedicalHistoryMapper medicalHistoryMapper;

    @Override
    public MedicalHistoryResponse addMedicalHistory(MedicalHistoryRequest request) {


        if (request.getMedicalRecordId() == null || request.getType() == null
                || request.getDescription() == null || request.getStatus() == null) {
            return null;
        }

        MedicalRecord record = medicalRecordRepository.findById(request.getMedicalRecordId()).orElse(null);
        if (record == null) return null;

        MedicalHistory history = MedicalHistory.builder()
                .medicalRecord(record)
                .type(request.getType())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        return medicalHistoryMapper.toDto(medicalHistoryRepository.save(history));
    }

    @Override
    public MedicalHistoryResponse selectMedicalHistoryByIdWithGet(Long id) {
        MedicalHistory history = medicalHistoryRepository.findById(id).get();
        return medicalHistoryMapper.toDto(history);
    }

    @Override
    public MedicalHistoryResponse selectMedicalHistoryByIdWithOrElse(Long id) {
        MedicalHistory history = medicalHistoryRepository.findById(id).orElse(null);
        if (history == null) return null;
        return medicalHistoryMapper.toDto(history);
    }

    @Override
    public List<MedicalHistoryResponse> selectAllMedicalHistories() {
        List<MedicalHistory> histories = medicalHistoryRepository.findAll();
        List<MedicalHistoryResponse> responses = new ArrayList<>();
        for (MedicalHistory h : histories) {
            responses.add(medicalHistoryMapper.toDto(h));
        }
        return responses;
    }

    @Override
    public MedicalHistoryResponse updateMedicalHistory(Long id, MedicalHistoryRequest request) {
        MedicalHistory history = medicalHistoryRepository.findById(id).orElse(null);
        if (history == null) return null;

        if (request.getMedicalRecordId() != null) {
            MedicalRecord record = medicalRecordRepository.findById(request.getMedicalRecordId()).orElse(null);
            if (record == null) return null;
            history.setMedicalRecord(record);
        }

        if (request.getType() != null) history.setType(request.getType());
        if (request.getDescription() != null) history.setDescription(request.getDescription());
        if (request.getStartDate() != null) history.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) history.setEndDate(request.getEndDate());
        if (request.getStatus() != null) history.setStatus(request.getStatus());

        return medicalHistoryMapper.toDto(medicalHistoryRepository.save(history));
    }

    @Override
    public void deleteMedicalHistoryById(Long id) {
        medicalHistoryRepository.deleteById(id);
    }

    @Override
    public void deleteAllMedicalHistories() {
        medicalHistoryRepository.deleteAll();
    }

    @Override
    public long countingMedicalHistories() {
        return medicalHistoryRepository.count();
    }

    @Override
    public boolean verifMedicalHistoryById(Long id) {
        return medicalHistoryRepository.existsById(id);
    }
}