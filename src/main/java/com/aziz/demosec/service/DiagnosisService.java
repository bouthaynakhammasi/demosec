package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Diagnosis;
import com.aziz.demosec.Entities.DiagnosisType;
import com.aziz.demosec.Mapper.DiagnosisMapper;
import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.DiagnosisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DiagnosisService implements IDiagnosisService {

    private DiagnosisRepository diagnosisRepository;
    private ConsultationRepository consultationRepository;
    private DiagnosisMapper diagnosisMapper;

    @Override
    @Transactional
    public DiagnosisResponse addDiagnosis(DiagnosisRequest request) {
        System.out.println("DEBUG: addDiagnosis called with " + request);

        if (request.getConsultationId() == null || request.getDescription() == null || request.getType() == null) {
            System.out.println("DEBUG: Missing required fields in DiagnosisRequest");
            return null;
        }

        Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
        if (consultation == null) {
            System.out.println("DEBUG: Consultation NOT FOUND for ID " + request.getConsultationId());
            return null;
        }

        Diagnosis diagnosis = Diagnosis.builder()
                .consultation(consultation)
                .description(request.getDescription())
                .type(safeMapType(request.getType()))
                .build();

        Diagnosis saved = diagnosisRepository.save(diagnosis);
        System.out.println("DEBUG: Diagnosis saved successfully with ID: " + saved.getId());
        return diagnosisMapper.toDto(saved);
    }

    private DiagnosisType safeMapType(String typeStr) {
        try {
            return DiagnosisType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            System.out.println("DEBUG: Invalid DiagnosisType '" + typeStr + "', falling back to PRIMARY");
            return DiagnosisType.PRIMARY;
        }
    }

    @Override
    public DiagnosisResponse selectDiagnosisByIdWithGet(Long id) {
        Diagnosis d = diagnosisRepository.findById(id).get();
        return diagnosisMapper.toDto(d);
    }

    @Override
    public DiagnosisResponse selectDiagnosisByIdWithOrElse(Long id) {
        Diagnosis d = diagnosisRepository.findById(id).orElse(null);
        if (d == null) return null;
        return diagnosisMapper.toDto(d);
    }

    @Override
    public List<DiagnosisResponse> selectAllDiagnoses() {
        List<Diagnosis> list = diagnosisRepository.findAll();
        List<DiagnosisResponse> responses = new ArrayList<>();
        for (Diagnosis d : list) {
            responses.add(diagnosisMapper.toDto(d));
        }
        return responses;
    }

    @Override
    @Transactional
    public DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request) {
        System.out.println("DEBUG: updateDiagnosis called for ID " + id);

        Diagnosis diagnosis = diagnosisRepository.findById(id).orElse(null);
        if (diagnosis == null) {
            System.out.println("DEBUG: Diagnosis NOT FOUND for ID " + id);
            return null;
        }

        if (request.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
            if (consultation == null) {
                System.out.println("DEBUG: Consultation NOT FOUND for ID " + request.getConsultationId());
                return null;
            }
            diagnosis.setConsultation(consultation);
        }

        if (request.getDescription() != null) diagnosis.setDescription(request.getDescription());
        if (request.getType() != null) diagnosis.setType(safeMapType(request.getType()));

        Diagnosis updated = diagnosisRepository.save(diagnosis);
        System.out.println("DEBUG: Diagnosis updated successfully");
        return diagnosisMapper.toDto(updated);
    }

    @Override
    public void deleteDiagnosisById(Long id) {
        diagnosisRepository.deleteById(id);
    }

    @Override
    public void deleteAllDiagnoses() {
        diagnosisRepository.deleteAll();
    }

    @Override
    public long countingDiagnoses() {
        return diagnosisRepository.count();
    }

    @Override
    public boolean verifDiagnosisById(Long id) {
        return diagnosisRepository.existsById(id);
    }
}