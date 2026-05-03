package com.aziz.demosec.controller;

import com.aziz.demosec.entities.HomeCareService;
import com.aziz.demosec.repository.HomeCareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home-care-services")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class HomeCareController {

    private final HomeCareServiceRepository homeCareServiceRepository;

    @GetMapping
    public List<HomeCareService> getAllServices() {
        return homeCareServiceRepository.findAll();
    }

    @PostMapping
    public HomeCareService createService(@RequestBody HomeCareService service) {
        return homeCareServiceRepository.save(service);
    }
}
