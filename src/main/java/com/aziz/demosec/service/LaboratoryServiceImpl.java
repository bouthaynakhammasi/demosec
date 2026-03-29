package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.LaboratoryMapper;
import com.aziz.demosec.repository.LaboratoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaboratoryServiceImpl implements Laboratoryservice {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;

    @Override
    public LaboratoryResponse create(LaboratoryRequest request) {
        if (laboratoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Laboratory '" + request.getName() + "' already exists");
        }
        return laboratoryMapper.toDto(laboratoryRepository.save(laboratoryMapper.toEntity(request)));
    }

    @Override
    public LaboratoryResponse getById(Long id) {
        return laboratoryMapper.toDto(findOrThrow(id));
    }

    @Override
    public List<LaboratoryResponse> getAll() {
        List<LaboratoryResponse> responses = new ArrayList<>();
        for (Laboratory lab : laboratoryRepository.findAll()) {
            responses.add(laboratoryMapper.toDto(lab));
        }
        return responses;
    }

    @Override
    public List<LaboratoryResponse> searchByName(String name) {
        List<LaboratoryResponse> responses = new ArrayList<>();
        for (Laboratory lab : laboratoryRepository.findByNameContainingIgnoreCase(name)) {
            responses.add(laboratoryMapper.toDto(lab));
        }
        return responses;
    }

    @Override
    public List<LaboratoryResponse> getActive() {
        List<LaboratoryResponse> responses = new ArrayList<>();
        for (Laboratory lab : laboratoryRepository.findByActiveTrue()) {
            responses.add(laboratoryMapper.toDto(lab));
        }
        return responses;
    }

    @Override
    public LaboratoryResponse update(Long id, LaboratoryRequest request) {
        Laboratory lab = findOrThrow(id);
        laboratoryMapper.updateFromDto(request, lab);
        return laboratoryMapper.toDto(laboratoryRepository.save(lab));
    }

    @Override
    public LaboratoryResponse toggleActive(Long id) {
        Laboratory lab = findOrThrow(id);
        lab.setActive(!lab.isActive());
        return laboratoryMapper.toDto(laboratoryRepository.save(lab));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        laboratoryRepository.deleteById(id);
    }

    private Laboratory findOrThrow(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + id));
    }
}