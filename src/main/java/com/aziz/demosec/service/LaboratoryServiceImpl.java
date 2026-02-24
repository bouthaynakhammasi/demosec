package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LaboratoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratoryServiceImpl implements Laboratoryservice.LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;

    @Override
    public LaboratoryResponse create(LaboratoryRequest request) {
        if (laboratoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Laboratory with name '" + request.getName() + "' already exists");
        }
        Laboratory laboratory = Laboratory.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .build();
        return toResponse(laboratoryRepository.save(laboratory));
    }

    @Override
    public LaboratoryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<LaboratoryResponse> getAll() {
        return laboratoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LaboratoryResponse update(Long id, LaboratoryRequest request) {
        Laboratory laboratory = findOrThrow(id);
        laboratory.setName(request.getName());
        laboratory.setAddress(request.getAddress());
        laboratory.setPhone(request.getPhone());
        return toResponse(laboratoryRepository.save(laboratory));
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

    private LaboratoryResponse toResponse(Laboratory laboratory) {
        return LaboratoryResponse.builder()
                .id(laboratory.getId())
                .name(laboratory.getName())
                .address(laboratory.getAddress())
                .phone(laboratory.getPhone())
                .build();
    }
}