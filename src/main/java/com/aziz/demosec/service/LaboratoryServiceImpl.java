package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LaboratoryStaff;
import com.aziz.demosec.Mapper.LaboratoryMapper;
import com.aziz.demosec.dto.LaboratoryRequest;
import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LaboratoryStaffRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaboratoryServiceImpl implements ILaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryStaffRepository laboratoryStaffRepository;
    private final UserRepository userRepository;
    private final LaboratoryMapper laboratoryMapper;

    // --- Profile Methods ---
    @Override
    public LaboratoryResponse getLaboratoryForCurrentUser(String email) {
        return laboratoryStaffRepository.findByEmail(email)
                .map(staff -> {
                    Laboratory laboratory = staff.getLaboratory();
                    if (laboratory == null) {
                        return new LaboratoryResponse();
                    }
                    return laboratoryMapper.toDto(laboratory);
                })
                .orElseGet(() -> {
                    var userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent() && userOpt.get().getRole() == Role.LABORATORY_STAFF) {
                        var user = userOpt.get();
                        if (user instanceof LaboratoryStaff) {
                            LaboratoryStaff staff = (LaboratoryStaff) user;
                            if (staff.getLaboratory() == null) {
                                return new LaboratoryResponse();
                            }
                            return laboratoryMapper.toDto(staff.getLaboratory());
                        }
                        return new LaboratoryResponse();
                    }
                    throw new RuntimeException("Laboratory Staff not found for email: " + email);
                });
    }

    @Override
    @Transactional
    public LaboratoryResponse updateProfile(String email, LaboratoryStaffProfileUpdateRequest request) {
        LaboratoryStaff staff = laboratoryStaffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Laboratory Staff not found"));

        if (request.fullName() != null) staff.setFullName(request.fullName());
        if (request.phone() != null) staff.setPhone(request.phone());
        if (request.birthDate() != null) staff.setBirthDate(request.birthDate());
        if (request.photo() != null) staff.setPhoto(request.photo());

        Laboratory laboratory = staff.getLaboratory();
        if (laboratory == null) {
            laboratory = new Laboratory();
            laboratory.setName(request.laboratoryName() != null ? request.laboratoryName() : "Unnamed Laboratory");
        }

        if (request.laboratoryName() != null) laboratory.setName(request.laboratoryName());
        if (request.laboratoryAddress() != null) laboratory.setAddress(request.laboratoryAddress());
        if (request.laboratoryPhone() != null) laboratory.setPhone(request.laboratoryPhone());

        Laboratory savedLaboratory = laboratoryRepository.save(laboratory);
        staff.setLaboratory(savedLaboratory);
        laboratoryStaffRepository.save(staff);

        return laboratoryMapper.toDto(savedLaboratory);
    }

    // --- CRUD Methods ---
    @Override
    @Transactional
    public LaboratoryResponse create(LaboratoryRequest request) {
        if (laboratoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Laboratory '" + request.getName() + "' already exists");
        }
        Laboratory lab = laboratoryMapper.toEntity(request);
        return laboratoryMapper.toDto(laboratoryRepository.save(lab));
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
    @Transactional
    public LaboratoryResponse update(Long id, LaboratoryRequest request) {
        Laboratory lab = findOrThrow(id);
        laboratoryMapper.updateFromDto(request, lab);
        return laboratoryMapper.toDto(laboratoryRepository.save(lab));
    }

    @Override
    @Transactional
    public LaboratoryResponse toggleActive(Long id) {
        Laboratory lab = findOrThrow(id);
        lab.setActive(!lab.isActive());
        return laboratoryMapper.toDto(laboratoryRepository.save(lab));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        laboratoryRepository.deleteById(id);
    }

    private Laboratory findOrThrow(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with id: " + id));
    }
}