package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalEventCreateRequest;
import com.aziz.demosec.dto.MedicalEventUpdateRequest;
import com.aziz.demosec.dto.MedicalEventResponse;
import com.aziz.demosec.service.IMedicalEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class MedicalEventController {

    private final IMedicalEventService medicalEventService;

    @GetMapping
    public List<MedicalEventResponse> getAll() {
        return medicalEventService.getAll();
    }

    @GetMapping("/{id}")
    public MedicalEventResponse getById(@PathVariable Long id) {
        return medicalEventService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalEventResponse create(@Valid @RequestBody MedicalEventCreateRequest request) {
        return medicalEventService.create(request);
    }

    @PutMapping("/{id}")
    public MedicalEventResponse update(@PathVariable Long id,
                                       @Valid @RequestBody MedicalEventUpdateRequest request) {
        return medicalEventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        medicalEventService.delete(id);
    }
}