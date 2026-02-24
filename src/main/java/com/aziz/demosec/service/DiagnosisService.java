package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Diagnosis;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.DiagnosisRepository;
import com.aziz.demosec.dto.DiagnosisRequest;
import com.aziz.demosec.dto.DiagnosisResponse;
import com.aziz.demosec.Mapper.DiagnosisMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DiagnosisService implements IDiagnosisService {

    private DiagnosisRepository diagnosisRepository;
    private ConsultationRepository consultationRepository;
    private DiagnosisMapper diagnosisMapper;

    @Override
    public DiagnosisResponse addDiagnosis(DiagnosisRequest request) {

        if (request.getConsultationId() == null ||
                request.getDescription() == null ||
                request.getType() == null)
            return null;

        Consultation consultation =
                consultationRepository.findById(request.getConsultationId()).orElse(null);

        if (consultation == null) return null;

        Diagnosis diagnosis = Diagnosis.builder()
                .consultation(consultation)
                .description(request.getDescription())
                .type(request.getType())
                .build();

        return diagnosisMapper.toDto(diagnosisRepository.save(diagnosis));
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
    public DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request) {

        Diagnosis diagnosis = diagnosisRepository.findById(id).orElse(null);
        if (diagnosis == null) return null;

        if (request.getConsultationId() != null) {
            Consultation consultation =
                    consultationRepository.findById(request.getConsultationId()).orElse(null);
            if (consultation == null) return null;
            diagnosis.setConsultation(consultation);
        }

        if (request.getDescription() != null)
            diagnosis.setDescription(request.getDescription());

        if (request.getType() != null)
            diagnosis.setType(request.getType());

        return diagnosisMapper.toDto(diagnosisRepository.save(diagnosis));
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