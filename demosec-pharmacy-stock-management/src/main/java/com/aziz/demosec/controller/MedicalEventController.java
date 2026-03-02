package com.aziz.demosec.controller;

import com.aziz.demosec.dto.request.MedicalEventCreateRequest;
import com.aziz.demosec.dto.request.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.response.MedicalEventResponse;
import com.aziz.demosec.entities.MedicalEventType;
import com.aziz.demosec.service.IMedicalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class MedicalEventController {

    private final IMedicalEventService medicalEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalEventResponse create(@RequestBody MedicalEventCreateRequest request) {
        return medicalEventService.create(request);
    }

    @PutMapping("/{id}")
    public MedicalEventResponse update(@PathVariable Long id, @RequestBody MedicalEventUpdateRequest request) {
        return medicalEventService.update(id, request);
    }

    @GetMapping("/{id}")
    public MedicalEventResponse getById(@PathVariable Long id) {
        return medicalEventService.getById(id);
    }

    @GetMapping
    public List<MedicalEventResponse> getAll() {
        return medicalEventService.getAll();
    }

    @GetMapping("/upcoming")
    public List<MedicalEventResponse> upcoming() {
        return medicalEventService.getUpcoming();
    }

    @GetMapping("/type/{type}")
    public List<MedicalEventResponse> byType(@PathVariable MedicalEventType type) {
        return medicalEventService.getByType(type);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        medicalEventService.delete(id);
    }
}