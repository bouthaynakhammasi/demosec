package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ProviderCalendarRequest;
import com.aziz.demosec.dto.ProviderCalendarResponse;
import com.aziz.demosec.service.IProviderCalendarService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/provider-calendar")
public class ProviderCalendarController {

    private IProviderCalendarService calendarService;

    @PostMapping("/add")
    public ProviderCalendarResponse addCalendar(@RequestBody ProviderCalendarRequest request) {
        return calendarService.addCalendar(request);
    }

    @GetMapping("/get/{id}")
    public ProviderCalendarResponse getCalendar(@PathVariable Long id) {
        return calendarService.selectCalendarByIdWithGet(id);
    }

    @GetMapping("/all")
    public List<ProviderCalendarResponse> getAllCalendars() {
        return calendarService.selectAllCalendars();
    }

    @PutMapping("/update/{id}")
    public ProviderCalendarResponse updateCalendar(@PathVariable Long id,
                                                   @RequestBody ProviderCalendarRequest request) {
        return calendarService.updateCalendar(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCalendar(@PathVariable Long id) {
        calendarService.deleteCalendarById(id);
    }
}
