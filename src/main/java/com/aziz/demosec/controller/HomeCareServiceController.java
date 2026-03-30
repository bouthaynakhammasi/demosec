package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.HomeCareService;
import com.aziz.demosec.repository.HomeCareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/home-care-services")
@RequiredArgsConstructor
public class HomeCareServiceController {

    private final HomeCareServiceRepository homeCareServiceRepository;

    public record HomeCareServiceDto(Long id, String name, String description, BigDecimal price) {
    }

    @GetMapping
    public ResponseEntity<List<HomeCareServiceDto>> getAllServices() {
        List<HomeCareServiceDto> services = homeCareServiceRepository.findAll()
                .stream()
                .map(s -> new HomeCareServiceDto(s.getId(), s.getName(), s.getDescription(), s.getPrice()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
}
