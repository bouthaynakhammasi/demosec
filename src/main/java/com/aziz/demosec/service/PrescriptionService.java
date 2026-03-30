package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Consultation;
import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Entities.PrescriptionItem;
import com.aziz.demosec.repository.ConsultationRepository;
import com.aziz.demosec.repository.PrescriptionRepository;
import com.aziz.demosec.dto.PrescriptionItemRequest;
import com.aziz.demosec.dto.PrescriptionRequest;
import com.aziz.demosec.dto.PrescriptionResponse;
import com.aziz.demosec.Mapper.PrescriptionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PrescriptionService implements IPrescriptionService {

    private PrescriptionRepository prescriptionRepository;
    private ConsultationRepository consultationRepository;
    private PrescriptionMapper prescriptionMapper;

    @Override
    @Transactional
    public PrescriptionResponse addPrescription(PrescriptionRequest request) {
        System.out.println("DEBUG: addPrescription called with " + request);

        if (request.getConsultationId() == null || request.getDate() == null) {
            System.out.println("DEBUG: Missing required fields in PrescriptionRequest");
            return null;
        }

        Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
        if (consultation == null) {
            System.out.println("DEBUG: Consultation NOT FOUND for ID " + request.getConsultationId());
            return null;
        }

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .date(parseDate(request.getDate()))
                .build();

        if (prescription.getItems() == null) {
            prescription.setItems(new ArrayList<>());
        }

        // Support flat payload (only if nested items are not provided)
        if (request.getMedication() != null && !request.getMedication().trim().isEmpty() && (request.getItems() == null || request.getItems().isEmpty())) {
            PrescriptionItem item = PrescriptionItem.builder()
                    .medicationName(request.getMedication())
                    .dosage(request.getDosage())
                    .duration(request.getInstructions()) 
                    .prescription(prescription)
                    .build();
            prescription.getItems().add(item);
        }

        // Support nested items (cascade)
        if (request.getItems() != null) {
            for (PrescriptionItemRequest itemRequest : request.getItems()) {
                if (itemRequest.getMedicationName() == null || itemRequest.getMedicationName().isEmpty()) continue;

                PrescriptionItem item = PrescriptionItem.builder()
                        .medicationName(itemRequest.getMedicationName())
                        .dosage(itemRequest.getDosage())
                        .frequency(itemRequest.getFrequency())
                        .duration(itemRequest.getDuration())
                        .prescription(prescription)
                        .build();

                prescription.getItems().add(item);
            }
        }

        // Final check: must have at least one item
        if (prescription.getItems().isEmpty() && (request.getMedication() == null || request.getMedication().isEmpty())) {
            System.out.println("DEBUG: No items found in prescription request");
            return null;
        }

        Prescription saved = prescriptionRepository.save(prescription);
        System.out.println("DEBUG: Prescription saved successfully with ID: " + saved.getId());
        return prescriptionMapper.toDto(saved);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("DEBUG: Failed to parse date '" + dateStr + "', using today");
            return LocalDate.now();
        }
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
    @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        System.out.println("DEBUG: updatePrescription called for ID " + id);

        Prescription prescription = prescriptionRepository.findById(id).orElse(null);
        if (prescription == null) return null;

        if (request.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(request.getConsultationId()).orElse(null);
            if (consultation != null) prescription.setConsultation(consultation);
        }

        if (request.getDate() != null) {
            prescription.setDate(parseDate(request.getDate()));
        }

        if (request.getItems() != null || request.getMedication() != null) {
            prescription.getItems().clear();
            
            // Re-add from flat payload if present and no nested items
            if (request.getMedication() != null && !request.getMedication().trim().isEmpty() && (request.getItems() == null || request.getItems().isEmpty())) {
                PrescriptionItem item = PrescriptionItem.builder()
                        .medicationName(request.getMedication())
                        .dosage(request.getDosage())
                        .duration(request.getInstructions())
                        .prescription(prescription)
                        .build();
                prescription.getItems().add(item);
            }

            // Add from nested items
            if (request.getItems() != null) {
                for (PrescriptionItemRequest itemRequest : request.getItems()) {
                    if (itemRequest.getMedicationName() == null || itemRequest.getMedicationName().isEmpty()) continue;
                    PrescriptionItem item = PrescriptionItem.builder()
                            .medicationName(itemRequest.getMedicationName())
                            .dosage(itemRequest.getDosage())
                            .frequency(itemRequest.getFrequency())
                            .duration(itemRequest.getDuration())
                            .prescription(prescription)
                            .build();
                    prescription.getItems().add(item);
                }
            }
        }

        Prescription updated = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDto(updated);
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