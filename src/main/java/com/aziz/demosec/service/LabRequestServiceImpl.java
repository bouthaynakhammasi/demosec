package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.LabRequestMapper;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabRequestServiceImpl implements LabRequestService {

    private final LabRequestRepository labRequestRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final LabRequestMapper labRequestMapper;

    @Override
    public LabRequestResponse create(LabRequestRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        LabRequest labRequest = labRequestMapper.toEntity(request);
        labRequest.setPatient(patient);
        labRequest.setDoctor(doctor);
        labRequest.setLaboratory(laboratory);
        labRequest.setStatus(LabRequestStatus.PENDING);
        labRequest.setRequestedAt(LocalDateTime.now());

        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
    }

    @Override
    public LabRequestResponse getById(Long id) {
        return labRequestMapper.toDto(findOrThrow(id));
    }

    @Override
    public List<LabRequestResponse> getAll() {
        return labRequestRepository.findAll()
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabRequestResponse> getByPatient(Long patientId) {
        return labRequestRepository.findByPatientId(patientId)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabRequestResponse> getByDoctor(Long doctorId) {
        return labRequestRepository.findByDoctorId(doctorId)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabRequestResponse> getByLaboratory(Long laboratoryId) {
        return labRequestRepository.findByLaboratoryId(laboratoryId)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabRequestResponse updateStatus(Long id, LabRequestStatus status) {
        LabRequest labRequest = findOrThrow(id);
        labRequest.setStatus(status);
        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
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
}