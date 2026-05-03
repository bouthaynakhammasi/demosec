package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LaboratoryStaff;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.Mapper.LaboratoryMapper;
import com.aziz.demosec.repository.LaboratoryStaffRepository;
import com.aziz.demosec.repository.LaboratoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaboratoryServiceImpl implements LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryStaffRepository laboratoryStaffRepository;
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

    @Override
    public LaboratoryResponse getMyLaboratory(String email) {
        LaboratoryStaff staff = laboratoryStaffRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory Staff not found with email: " + email));
        
        if (staff.getLaboratory() == null) {
            throw new ResourceNotFoundException("No laboratory associated with this staff member");
        }
        
        return laboratoryMapper.toDto(staff.getLaboratory());
    }

    @Override
    public LaboratoryResponse updateProfile(String email, com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest request) {
        LaboratoryStaff staff = laboratoryStaffRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory Staff not found with email: " + email));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            staff.setFullName(request.fullName());
        }
        if (request.phone() != null) staff.setPhone(request.phone());
        if (request.birthDate() != null) staff.setBirthDate(request.birthDate());
        if (request.photo() != null) staff.setPhoto(request.photo());

        Laboratory laboratory = staff.getLaboratory();
        if (laboratory != null) {
            if (request.laboratoryName() != null && !request.laboratoryName().isBlank()) {
                laboratory.setName(request.laboratoryName());
            }
            if (request.laboratoryAddress() != null) laboratory.setAddress(request.laboratoryAddress());
            if (request.laboratoryPhone() != null) laboratory.setPhone(request.laboratoryPhone());
            if (request.laboratoryEmail() != null) laboratory.setEmail(request.laboratoryEmail());
            if (request.openingHours() != null) laboratory.setOpeningHours(request.openingHours());
            if (request.specializations() != null) laboratory.setSpecializations(request.specializations());
            if (request.active() != null) laboratory.setActive(request.active());
            
            laboratoryRepository.save(laboratory);
        }

        laboratoryStaffRepository.save(staff);
        return laboratoryMapper.toDto(laboratory);
    }

    private Laboratory findOrThrow(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + id));
    }
}