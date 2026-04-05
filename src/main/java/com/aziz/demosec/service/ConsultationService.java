package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;
import com.aziz.demosec.Mapper.ConsultationMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ConsultationService implements IConsultationService {

    private ConsultationRepository consultationRepository;
    private MedicalRecordRepository medicalRecordRepository;
    private UserRepository userRepository;
    private ConsultationMapper consultationMapper;

    @Override
    @Transactional
    public ConsultationResponse addConsultation(ConsultationRequest request) {
        System.out.println("DEBUG: addConsultation called with " + request);

        if (request.getMedicalRecordId() == null ||
                request.getDoctorId() == null ||
                request.getDate() == null) {
            System.out.println("DEBUG: Missing required fields in ConsultationRequest");
            return null;
        }

        MedicalRecord medicalRecord =
                medicalRecordRepository.findById(request.getMedicalRecordId()).orElse(null);

        User doctor =
                userRepository.findById(request.getDoctorId()).orElse(null);

        if (medicalRecord == null) {
            System.out.println("DEBUG: MedicalRecord NOT FOUND for ID " + request.getMedicalRecordId());
            return null;
        }
        if (doctor == null) {
            System.out.println("DEBUG: Doctor NOT FOUND for ID " + request.getDoctorId());
            return null;
        }

        Consultation consultation = Consultation.builder()
                .medicalRecord(medicalRecord)
                .doctor(doctor)
                .date(request.getDate())
                .observations(request.getObservations())
                .notes(request.getNotes())
                .build();

        Consultation saved = consultationRepository.save(consultation);
        System.out.println("DEBUG: Consultation saved successfully with ID: " + saved.getId());

        return consultationMapper.toDto(saved);
    }

    @Override
    public ConsultationResponse selectConsultationByIdWithGet(Long id) {
        Consultation c = consultationRepository.findById(id).get();
        return consultationMapper.toDto(c);
    }

    @Override
    public ConsultationResponse selectConsultationByIdWithOrElse(Long id) {
        Consultation c = consultationRepository.findById(id).orElse(null);
        if (c == null) return null;
        return consultationMapper.toDto(c);
    }

    @Override
    public List<ConsultationResponse> selectAllConsultations() {
        List<Consultation> list = consultationRepository.findAll();
        List<ConsultationResponse> responses = new ArrayList<>();
        for (Consultation c : list) {
            responses.add(consultationMapper.toDto(c));
        }
        return responses;
    }

    @Override
    @Transactional
    public ConsultationResponse updateConsultation(Long id, ConsultationRequest request) {
        System.out.println("DEBUG: updateConsultation called for ID " + id + " with " + request);

        Consultation consultation = consultationRepository.findById(id).orElse(null);
        if (consultation == null) {
            System.out.println("DEBUG: Consultation NOT FOUND for ID " + id);
            return null;
        }

        if (request.getMedicalRecordId() != null) {
            MedicalRecord mr =
                    medicalRecordRepository.findById(request.getMedicalRecordId()).orElse(null);
            if (mr == null) {
                System.out.println("DEBUG: MedicalRecord NOT FOUND for ID " + request.getMedicalRecordId());
                return null;
            }
            consultation.setMedicalRecord(mr);
        }

        if (request.getDoctorId() != null) {
            User doctor =
                    userRepository.findById(request.getDoctorId()).orElse(null);
            if (doctor == null) {
                System.out.println("DEBUG: Doctor NOT FOUND for ID " + request.getDoctorId());
                return null;
            }
            consultation.setDoctor(doctor);
        }
        
        // ... rest of update logic ... (keep existing)
        if (request.getDate() != null)
            consultation.setDate(request.getDate());

        if (request.getObservations() != null)
            consultation.setObservations(request.getObservations());

        if (request.getNotes() != null)
            consultation.setNotes(request.getNotes());

        Consultation updated = consultationRepository.save(consultation);
        System.out.println("DEBUG: Consultation updated successfully");
        return consultationMapper.toDto(updated);
    }

    @Override
    public void deleteConsultationById(Long id) {
        consultationRepository.deleteById(id);
    }

    @Override
    public void deleteAllConsultations() {
        consultationRepository.deleteAll();
    }

    @Override
    public long countingConsultations() {
        return consultationRepository.count();
    }

    @Override
    public boolean verifConsultationById(Long id) {
        return consultationRepository.existsById(id);
    }
}