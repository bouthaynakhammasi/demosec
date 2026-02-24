package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Entities.PrescriptionItem;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.PrescriptionRepository;
import com.aziz.demosec.dto.PrescriptionItemRequest;
import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;
import com.aziz.demosec.mapper.PrescriptionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PrescriptionService implements IPrescriptionService {

    private PrescriptionRepository prescriptionRepository;
    private ConsultationRepository consultationRepository;
    private PrescriptionMapper prescriptionMapper;

    @Override
    public PrescriptionResponse addPrescription(PrescriptionRequest request) {

        if (request.getConsultationId() == null || request.getDate() == null) return null;

        Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
        if (consultation == null) return null;

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .date(request.getDate())
                .build();

        if (prescription.getItems() == null) {
            prescription.setItems(new ArrayList<>());
        }

        // add items (cascade)
        if (request.getItems() != null) {
            for (PrescriptionItemRequest itemRequest : request.getItems()) {

                // medicationName required
                if (itemRequest.getMedicationName() == null) return null;

                PrescriptionItem item = PrescriptionItem.builder()
                        .medicationName(itemRequest.getMedicationName())
                        .dosage(itemRequest.getDosage())
                        .frequency(itemRequest.getFrequency())
                        .duration(itemRequest.getDuration())
                        .prescription(prescription) // IMPORTANT
                        .build();

                prescription.getItems().add(item);
            }
        }

        return prescriptionMapper.toDto(prescriptionRepository.save(prescription));
    }

    @Override
    public PrescriptionResponse selectPrescriptionByIdWithGet(Long id) {
        Prescription p = prescriptionRepository.findById(id).get();
        return prescriptionMapper.toDto(p);
    }

    @Override
    public PrescriptionResponse selectPrescriptionByIdWithOrElse(Long id) {
        Prescription p = prescriptionRepository.findById(id).orElse(null);
        if (p == null) return null;
        return prescriptionMapper.toDto(p);
    }

    @Override
    public List<PrescriptionResponse> selectAllPrescriptions() {
        List<Prescription> list = prescriptionRepository.findAll();
        List<PrescriptionResponse> responses = new ArrayList<>();
        for (Prescription p : list) {
            responses.add(prescriptionMapper.toDto(p));
        }
        return responses;
    }

    @Override
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {

        Prescription prescription = prescriptionRepository.findById(id).orElse(null);
        if (prescription == null) return null;

        // update consultation if provided
        if (request.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
            if (consultation == null) return null;
            prescription.setConsultation(consultation);
        }

        // update date if provided
        if (request.getDate() != null) {
            prescription.setDate(request.getDate());
        }

        // replace items if provided
        if (request.getItems() != null) {

            // remove old items (requires orphanRemoval=true)
            prescription.getItems().clear();

            for (PrescriptionItemRequest itemRequest : request.getItems()) {

                if (itemRequest.getMedicationName() == null) return null;

                PrescriptionItem item = PrescriptionItem.builder()
                        .medicationName(itemRequest.getMedicationName())
                        .dosage(itemRequest.getDosage())
                        .frequency(itemRequest.getFrequency())
                        .duration(itemRequest.getDuration())
                        .prescription(prescription) // IMPORTANT
                        .build();

                prescription.getItems().add(item);
            }
        }

        return prescriptionMapper.toDto(prescriptionRepository.save(prescription));
    }

    @Override
    public void deletePrescriptionById(Long id) {
        prescriptionRepository.deleteById(id);
    }

    @Override
    public void deleteAllPrescriptions() {
        prescriptionRepository.deleteAll();
    }

    @Override
    public long countingPrescriptions() {
        return prescriptionRepository.count();
    }

    @Override
    public boolean verifPrescriptionById(Long id) {
        return prescriptionRepository.existsById(id);
    }
}