package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LabTest;
import com.aziz.demosec.dto.LabTestRequest;
import com.aziz.demosec.dto.LabTestResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.Mapper.LabTestMapper;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final LabTestMapper labTestMapper;

    @Override
    public LabTestResponse create(LabTestRequest request) {
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        if (labTestRepository.existsByNameAndLaboratoryId(request.getName(), request.getLaboratoryId())) {
            throw new IllegalArgumentException("LabTest '" + request.getName() + "' already exists in this laboratory");
        }

        LabTest labTest = labTestMapper.toEntity(request);
        labTest.setLaboratory(laboratory);

        return labTestMapper.toDto(labTestRepository.save(labTest));
    }

    @Override
    public LabTestResponse getById(Long id) {
        return labTestMapper.toDto(findOrThrow(id));
    }

    @Override
    public List<LabTestResponse> getAll() {
        return labTestRepository.findAll()
                .stream()
                .map(labTestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestResponse> getByLaboratory(Long laboratoryId) {
        return labTestRepository.findByLaboratoryId(laboratoryId)
                .stream()
                .map(labTestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabTestResponse update(Long id, LabTestRequest request) {
        LabTest labTest = findOrThrow(id);
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + request.getLaboratoryId()));

        labTestMapper.updateFromDto(request, labTest);
        labTest.setLaboratory(laboratory);

        return labTestMapper.toDto(labTestRepository.save(labTest));
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
}