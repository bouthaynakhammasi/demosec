package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Treatment;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.TreatmentRepository;
import com.aziz.demosec.dto.TreatmentRequest;
import com.aziz.demosec.dto.TreatmentResponse;
import com.aziz.demosec.Mapper.TreatmentMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TreatmentService implements ITreatmentService {

    private TreatmentRepository treatmentRepository;
    private ConsultationRepository consultationRepository;
    private TreatmentMapper treatmentMapper;

    @Override
    public TreatmentResponse addTreatment(TreatmentRequest request) {

        // required fields
        if (request.getConsultationId() == null ||
                request.getTreatmentType() == null ||
                request.getDescription() == null ||
                request.getStatus() == null)
            return null;

        Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
        if (consultation == null) return null;

        Treatment treatment = Treatment.builder()
                .consultation(consultation)
                .treatmentType(request.getTreatmentType())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        return treatmentMapper.toDto(treatmentRepository.save(treatment));
    }

    @Override
    public TreatmentResponse selectTreatmentByIdWithGet(Long id) {
        Treatment t = treatmentRepository.findById(id).get();
        return treatmentMapper.toDto(t);
    }

    @Override
    public TreatmentResponse selectTreatmentByIdWithOrElse(Long id) {
        Treatment t = treatmentRepository.findById(id).orElse(null);
        if (t == null) return null;
        return treatmentMapper.toDto(t);
    }

    @Override
    public List<TreatmentResponse> selectAllTreatments() {
        List<Treatment> list = treatmentRepository.findAll();
        List<TreatmentResponse> responses = new ArrayList<>();
        for (Treatment t : list) {
            responses.add(treatmentMapper.toDto(t));
        }
        return responses;
    }

    @Override
    public TreatmentResponse updateTreatment(Long id, TreatmentRequest request) {

        Treatment treatment = treatmentRepository.findById(id).orElse(null);
        if (treatment == null) return null;

        if (request.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
            if (consultation == null) return null;
            treatment.setConsultation(consultation);
        }

        if (request.getTreatmentType() != null) treatment.setTreatmentType(request.getTreatmentType());
        if (request.getDescription() != null) treatment.setDescription(request.getDescription());
        if (request.getStartDate() != null) treatment.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) treatment.setEndDate(request.getEndDate());
        if (request.getStatus() != null) treatment.setStatus(request.getStatus());

        return treatmentMapper.toDto(treatmentRepository.save(treatment));
    }

    @Override
    public void deleteTreatmentById(Long id) {
        treatmentRepository.deleteById(id);
    }

    @Override
    public void deleteAllTreatments() {
        treatmentRepository.deleteAll();
    }

    @Override
    public long countingTreatments() {
        return treatmentRepository.count();
    }

    @Override
    public boolean verifTreatmentById(Long id) {
        return treatmentRepository.existsById(id);
    }
}