package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabRequestServiceImpl implements LabRequestService {

    private final LabRequestRepository labRequestRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public LabRequestResponse create(LabRequestRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        LabRequest labRequest = LabRequest.builder()
                .patient(patient)
                .doctor(doctor)
                .laboratory(laboratory)
                .status(LabRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        return toResponse(labRequestRepository.save(labRequest));
    }

    @Override
    public LabRequestResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<LabRequestResponse> getAll() {
        List<LabRequest> requests = labRequestRepository.findAll();
        List<LabRequestResponse> responses = new ArrayList<>();
        for (LabRequest r : requests) {
            responses.add(toResponse(r));
        }
        return responses;
    }

    @Override
    public List<LabRequestResponse> getByPatient(Long patientId) {
        List<LabRequest> requests = labRequestRepository.findByPatientId(patientId);
        List<LabRequestResponse> responses = new ArrayList<>();
        for (LabRequest r : requests) {
            responses.add(toResponse(r));
        }
        return responses;
    }

    @Override
    public List<LabRequestResponse> getByDoctor(Long doctorId) {
        List<LabRequest> requests = labRequestRepository.findByDoctorId(doctorId);
        List<LabRequestResponse> responses = new ArrayList<>();
        for (LabRequest r : requests) {
            responses.add(toResponse(r));
        }
        return responses;
    }

    @Override
    public List<LabRequestResponse> getByLaboratory(Long laboratoryId) {
        List<LabRequest> requests = labRequestRepository.findByLaboratoryId(laboratoryId);
        List<LabRequestResponse> responses = new ArrayList<>();
        for (LabRequest r : requests) {
            responses.add(toResponse(r));
        }
        return responses;
    }

    @Override
    public LabRequestResponse updateStatus(Long id, LabRequestStatus status) {
        LabRequest labRequest = findOrThrow(id);
        labRequest.setStatus(status);
        return toResponse(labRequestRepository.save(labRequest));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        labRequestRepository.deleteById(id);
    }

    private LabRequest findOrThrow(Long id) {
        return labRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabRequest not found with id: " + id));
    }

    private LabRequestResponse toResponse(LabRequest r) {
        LabRequestResponse response = new LabRequestResponse();
        response.setId(r.getId());
        response.setPatientId(r.getPatient().getId());
        response.setPatientName(r.getPatient().getFullName());
        response.setDoctorId(r.getDoctor().getId());
        response.setDoctorName(r.getDoctor().getFullName());
        response.setLaboratoryId(r.getLaboratory().getId());
        response.setLaboratoryName(r.getLaboratory().getName());
        response.setStatus(r.getStatus());
        response.setRequestedAt(r.getRequestedAt());
        return response;
    }
}