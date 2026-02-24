package com.aziz.demosec.controller;

import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.service.ICalendarAvailabilityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/availability")
public class CalendarAvailabilityController {

    private ICalendarAvailabilityService availabilityService;

    @PostMapping("/add")
    public CalendarAvailabilityResponse addAvailability(@RequestBody CalendarAvailabilityRequest request) {
        return availabilityService.addAvailability(request);
    }

    @GetMapping("/get/{id}")
    public CalendarAvailabilityResponse getAvailabilityByIdWithGet(@PathVariable Long id) {
        return availabilityService.selectAvailabilityByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public CalendarAvailabilityResponse getAvailabilityByIdWithOrElse(@PathVariable Long id) {
        return availabilityService.selectAvailabilityByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<CalendarAvailabilityResponse> getAllAvailabilities() {
        return availabilityService.selectAllAvailabilities();
    }

    @PutMapping("/update/{id}")
    public CalendarAvailabilityResponse updateAvailability(@PathVariable Long id,
                                                           @RequestBody CalendarAvailabilityRequest request) {
        return availabilityService.updateAvailability(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAvailabilityById(@PathVariable Long id) {
        availabilityService.deleteAvailabilityById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAllAvailabilities() {
        availabilityService.deleteAllAvailabilities();
    }

    @GetMapping("/count")
    public long countAvailabilities() {
        return availabilityService.countingAvailabilities();
    }

    @GetMapping("/exists/{id}")
    public boolean existsAvailability(@PathVariable Long id) {
        return availabilityService.verifAvailabilityById(id);
    }
}