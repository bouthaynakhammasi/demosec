package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.LabRequestRequest;
import com.aziz.demosec.dto.LabRequestResponse;
import com.aziz.demosec.dto.RequestedBy;
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Laboratory not found with id: " + request.getLaboratoryId()));

        if (!laboratory.isActive()) {
            throw new IllegalArgumentException(
                    "Laboratory '" + laboratory.getName() + "' is currently inactive");
        }

        Doctor doctor = null;
        if (request.getDoctorId() != null) {
            doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Doctor not found with id: " + request.getDoctorId()));
        }

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
    public LabRequestResponse update(Long id, LabRequestRequest request) {
        LabRequest labRequest = findOrThrow(id);

        if (labRequest.getStatus() == LabRequestStatus.COMPLETED ||
                labRequest.getStatus() == LabRequestStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot update a request with status: " + labRequest.getStatus());
        }

        labRequestMapper.updateFromDto(request, labRequest);
        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
    }

    @Override
    public void delete(Long id) {
        LabRequest labRequest = findOrThrow(id);

        if (labRequest.getStatus() == LabRequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot delete a COMPLETED lab request.");
        }

        labRequestRepository.deleteById(id);
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
    public List<LabRequestResponse> getByStatus(LabRequestStatus status) {
        return labRequestRepository.findByStatus(status)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabRequestResponse> getByRequestedBy(RequestedBy requestedBy) {
        return labRequestRepository.findByRequestedBy(requestedBy)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabRequestResponse> getPatientHistory(Long patientId) {
        return labRequestRepository.findByPatientIdOrderByRequestedAtDesc(patientId)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabRequestResponse updateStatus(Long id, LabRequestStatus status) {
        LabRequest labRequest = findOrThrow(id);
        validateStatusTransition(labRequest.getStatus(), status);
        labRequest.setStatus(status);
        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
    }

    @Override
    public LabRequestResponse cancel(Long id) {
        LabRequest labRequest = findOrThrow(id);

        if (labRequest.getStatus() == LabRequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot cancel a COMPLETED lab request.");
        }

        labRequest.setStatus(LabRequestStatus.CANCELLED);
        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
    }

    @Override
    public LabRequestResponse markNotificationSent(Long id) {
        LabRequest labRequest = findOrThrow(id);

        if (labRequest.getStatus() != LabRequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Notification can only be sent for COMPLETED requests.");
        }

        labRequest.setNotificationSent(true);
        return labRequestMapper.toDto(labRequestRepository.save(labRequest));
    }

    private LabRequest findOrThrow(Long id) {
        return labRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabRequest not found with id: " + id));
    }

    private void validateStatusTransition(LabRequestStatus current, LabRequestStatus next) {
        if (current == LabRequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot change status of a COMPLETED request.");
        }
        if (current == LabRequestStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot change status of a CANCELLED request.");
        }

    }
    @Override
    public List<LabRequestResponse> getPendingByLaboratory(Long laboratoryId) {
        return labRequestRepository
                .findByLaboratoryIdAndStatus(laboratoryId, LabRequestStatus.PENDING)
                .stream()
                .map(labRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}