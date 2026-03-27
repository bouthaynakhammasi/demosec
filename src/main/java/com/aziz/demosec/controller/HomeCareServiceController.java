package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.HomeCareService;
import com.aziz.demosec.repository.HomeCareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/home-care-services")
@RequiredArgsConstructor
public class HomeCareServiceController {

    private final HomeCareServiceRepository homeCareServiceRepository;

    @GetMapping
    public List<HomeCareService> getAllServices() {
        return homeCareServiceRepository.findAll();
    }
}
