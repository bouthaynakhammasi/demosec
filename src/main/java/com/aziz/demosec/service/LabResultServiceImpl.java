package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LabRequest;
import com.aziz.demosec.Entities.LabRequestStatus;
import com.aziz.demosec.Entities.LabResult;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.LabResultRequest;
import com.aziz.demosec.dto.LabResultResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LabRequestRepository;
import com.aziz.demosec.repository.LabResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabResultServiceImpl implements LabResultService {

    private final LabResultRepository labResultRepository;
    private final LabRequestRepository labRequestRepository;

    @Override
    public LabResultResponse create(LabResultRequest request) {
        if (labResultRepository.existsByLabRequestId(request.getLabRequestId())) {
            throw new IllegalStateException("A result already exists for this lab request.");
        }

        LabRequest labRequest = labRequestRepository.findById(request.getLabRequestId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabRequest not found with id: " + request.getLabRequestId()));

        LabResult result = LabResult.builder()
                .labRequest(labRequest)
                .resultFile(request.getResultFile())
                .resultData(request.getResultData())
                .completedAt(LocalDateTime.now())
                .build();

        labRequest.setStatus(LabRequestStatus.COMPLETED);
        labRequestRepository.save(labRequest);

        return toResponse(labResultRepository.save(result));
    }

    @Override
    public LabResultResponse getById(Long id) {
        LabResult result = labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found with id: " + id));
        return toResponse(result);
    }

    @Override
    public LabResultResponse getByLabRequestId(Long labRequestId) {
        LabResult result = labResultRepository.findByLabRequestId(labRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found for labRequest id: " + labRequestId));
        return toResponse(result);
    }

    @Override
    public List<LabResultResponse> getAll() {
        return labResultRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LabResultResponse update(Long id, LabResultRequest request) {
        LabResult result = labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LabResult not found with id: " + id));

        result.setResultFile(request.getResultFile());
        result.setResultData(request.getResultData());
        result.setCompletedAt(LocalDateTime.now());

        return toResponse(labResultRepository.save(result));
    }

    @Override
    public void delete(Long id) {
        if (!labResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("LabResult not found with id: " + id);
        }
        labResultRepository.deleteById(id);
    }

    private LabResultResponse toResponse(LabResult result) {
        LabRequest req = result.getLabRequest();

        User patient = (User) req.getPatient();
        User doctor  = (User) req.getDoctor();

        return LabResultResponse.builder()
                .id(result.getId())
                .labRequestId(req.getId())
                .patientName(patient.getFullName())
                .doctorName(doctor.getFullName())
                // .doctorSpecialty SUPPRIMÉ
                .resultFile(result.getResultFile())
                .resultData(result.getResultData())
                .completedAt(result.getCompletedAt())
                .build();
    }
}