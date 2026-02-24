package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ConsultationRequest;
import com.aziz.demosec.dto.ConsultationResponse;
import com.aziz.demosec.service.IConsultationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/consultation")
public class ConsultationController {

    private IConsultationService consultationService;

    @PostMapping("/add")
    public ConsultationResponse add(@RequestBody ConsultationRequest request) {
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
                                       @RequestBody ConsultationRequest request) {
        return consultationService.updateConsultation(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        consultationService.deleteConsultationById(id);
    }
}