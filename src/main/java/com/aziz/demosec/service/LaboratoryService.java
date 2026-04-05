package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Laboratory;
import com.aziz.demosec.Entities.LaboratoryStaff;
import com.aziz.demosec.Mapper.LaboratoryMapper;
import com.aziz.demosec.dto.LaboratoryResponse;
import com.aziz.demosec.dto.LaboratoryStaffProfileUpdateRequest;
import com.aziz.demosec.repository.LaboratoryRepository;
import com.aziz.demosec.repository.LaboratoryStaffRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaboratoryService implements ILaboratoryService {

    private final LaboratoryStaffRepository laboratoryStaffRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;

    @Override
    public LaboratoryResponse getLaboratoryForCurrentUser(String email) {
        return laboratoryStaffRepository.findByEmail(email)
                .map(staff -> {
                    Laboratory laboratory = staff.getLaboratory();
                    if (laboratory == null) {
                        return new LaboratoryResponse();
                    }
                    return LaboratoryMapper.toResponse(laboratory);
                })
                .orElseGet(() -> {
                    // Auto-heal: Check if exists in User repository with role LABORATORYSTAFF
                    var userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent() && userOpt.get().getRole() == com.aziz.demosec.domain.Role.LABORATORYSTAFF) {
                        var user = userOpt.get();
                        LaboratoryStaff newStaff = new LaboratoryStaff();
                        newStaff.setId(user.getId());
                        newStaff.setEmail(user.getEmail());
                        newStaff.setFullName(user.getFullName());
                        newStaff.setRole(user.getRole());
                        newStaff.setPassword(user.getPassword());
                        newStaff.setPhone(user.getPhone());
                        newStaff.setEnabled(user.isEnabled());
                        newStaff.setBirthDate(user.getBirthDate());
                        
                        laboratoryStaffRepository.save(newStaff);
                        return new LaboratoryResponse();
                    }
                    throw new RuntimeException("Laboratory Staff not found");
                });
    }

    @Override
    @Transactional
    public LaboratoryResponse updateProfile(String email, LaboratoryStaffProfileUpdateRequest request) {
        LaboratoryStaff staff = laboratoryStaffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Laboratory Staff not found"));

        // Update personal info
        if (request.fullName() != null) staff.setFullName(request.fullName());
        if (request.phone() != null) staff.setPhone(request.phone());
        if (request.birthDate() != null) staff.setBirthDate(request.birthDate());
        if (request.photo() != null) staff.setPhoto(request.photo());

        // Update laboratory info
        Laboratory laboratory = staff.getLaboratory();
        if (laboratory == null) {
            laboratory = new Laboratory();
            // We need to set some default name if it's null because it's nullable=false in Entity
            laboratory.setName(request.laboratoryName() != null ? request.laboratoryName() : "Unnamed Laboratory");
            staff.setLaboratory(laboratory);
        }

        if (request.laboratoryName() != null) laboratory.setName(request.laboratoryName());
        if (request.laboratoryAddress() != null) laboratory.setAddress(request.laboratoryAddress());
        if (request.laboratoryPhone() != null) laboratory.setPhone(request.laboratoryPhone());

        laboratoryRepository.save(laboratory);
        LaboratoryStaff savedStaff = laboratoryStaffRepository.save(staff);

        return LaboratoryMapper.toResponse(savedStaff.getLaboratory());
    }
}
