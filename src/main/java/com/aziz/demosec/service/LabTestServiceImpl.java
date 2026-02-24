package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LabTest;
import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;
    private final LaboratoryRepository laboratoryRepository;

    @Override
    public LabTestResponse create(LabTestRequest request) {
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        if (labTestRepository.existsByNameAndLaboratoryId(request.getName(), request.getLaboratoryId())) {
            throw new IllegalArgumentException("LabTest '" + request.getName() + "' already exists in this laboratory");
        }

        LabTest labTest = LabTest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .laboratory(laboratory)
                .build();

        return toResponse(labTestRepository.save(labTest));
    }

    @Override
    public LabTestResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<LabTestResponse> getAll() {
        List<LabTest> labTests = labTestRepository.findAll();
        List<LabTestResponse> responses = new ArrayList<>();
        for (LabTest labTest : labTests) {
            responses.add(toResponse(labTest));
        }
        return responses;
    }

    @Override
    public List<LabTestResponse> getByLaboratory(Long laboratoryId) {
        List<LabTest> labTests = labTestRepository.findByLaboratoryId(laboratoryId);
        List<LabTestResponse> responses = new ArrayList<>();
        for (LabTest labTest : labTests) {
            responses.add(toResponse(labTest));
        }
        return responses;
    }

    @Override
    public LabTestResponse update(Long id, LabTestRequest request) {
        LabTest labTest = findOrThrow(id);
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        labTest.setName(request.getName());
        labTest.setDescription(request.getDescription());
        labTest.setPrice(request.getPrice());
        labTest.setLaboratory(laboratory);

        return toResponse(labTestRepository.save(labTest));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        labTestRepository.deleteById(id);
    }

    private LabTest findOrThrow(Long id) {
        return labTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest not found with id: " + id));
    }

    private LabTestResponse toResponse(LabTest labTest) {
        LabTestResponse response = new LabTestResponse();
        response.setId(labTest.getId());
        response.setName(labTest.getName());
        response.setDescription(labTest.getDescription());
        response.setPrice(labTest.getPrice());
        response.setLaboratoryId(labTest.getLaboratory().getId());
        response.setLaboratoryName(labTest.getLaboratory().getName());
        return response;
    }
}