package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;
import com.aziz.demosec.service.IConsultationService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RestController
@AllArgsConstructor
@RequestMapping("/consultation")
@Transactional
public class ConsultationController {

    private IConsultationService consultationService;

    @PostMapping("/add")
    public ConsultationResponse add(@Valid @RequestBody ConsultationRequest request) {
        return consultationService.addConsultation(request);
    }

    @GetMapping("/get/{id}")
    public ConsultationResponse getByIdWithGet(@PathVariable Long id) {
        return consultationService.selectConsultationByIdWithGet(id);
    }

    @GetMapping("/all")
    public List<ConsultationResponse> getAll() {
        return consultationService.selectAllConsultations();
    }

    @PutMapping("/update/{id}")
    public ConsultationResponse update(@PathVariable Long id,
                                       @Valid @RequestBody ConsultationRequest request) {
        return consultationService.updateConsultation(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        consultationService.deleteConsultationById(id);
    }
}