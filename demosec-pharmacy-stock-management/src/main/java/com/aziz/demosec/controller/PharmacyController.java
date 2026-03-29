package com.aziz.demosec.controller;

import com.aziz.demosec.dto.PharmacyRequest;
import com.aziz.demosec.dto.PharmacyResponse;
import com.aziz.demosec.service.IPharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final IPharmacyService pharmacyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PharmacyResponse create(@RequestBody PharmacyRequest request) {
        return pharmacyService.create(request);
    }

    @PutMapping("/{id}")
    public PharmacyResponse update(@PathVariable Long id, @RequestBody PharmacyRequest request) {
        return pharmacyService.update(id, request);
    }

    @GetMapping("/{id}")
    public PharmacyResponse getById(@PathVariable Long id) {
        return pharmacyService.getById(id);
    }

    @GetMapping
    public List<PharmacyResponse> getAll() {
        return pharmacyService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pharmacyService.delete(id);
    }
}