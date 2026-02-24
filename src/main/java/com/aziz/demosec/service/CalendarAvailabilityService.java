package com.aziz.demosec.service;

import com.aziz.demosec.Entities.CalendarAvailability;
import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.repository.CalendarAvailabilityRepository;
import com.aziz.demosec.repository.ProviderCalendarRepository;
import com.aziz.demosec.dto.CalendarAvailabilityRequest;
import com.aziz.demosec.dto.CalendarAvailabilityResponse;
import com.aziz.demosec.Mapper.CalendarAvailabilityMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CalendarAvailabilityService implements ICalendarAvailabilityService {

    private CalendarAvailabilityRepository availabilityRepository;
    private ProviderCalendarRepository providerCalendarRepository;
    private CalendarAvailabilityMapper mapper;

    @Override
    public CalendarAvailabilityResponse addAvailability(CalendarAvailabilityRequest request) {

        ProviderCalendar calendar = providerCalendarRepository.findById(request.getCalendarId()).orElse(null);
        if (calendar == null) return null;

        CalendarAvailability availability = CalendarAvailability.builder()
                .calendar(calendar)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .mode(request.getMode())
                .status(request.getStatus())
                .address(request.getAddress())
                .build();

        return mapper.toDto(availabilityRepository.save(availability));
    }

    @Override
    public CalendarAvailabilityResponse selectAvailabilityByIdWithGet(Long id) {
        CalendarAvailability availability = availabilityRepository.findById(id).get();
        return mapper.toDto(availability);
    }

    @Override
    public CalendarAvailabilityResponse selectAvailabilityByIdWithOrElse(Long id) {
        CalendarAvailability availability = availabilityRepository.findById(id).orElse(null);
        if (availability == null) return null;
        return mapper.toDto(availability);
    }

    @Override
    public List<CalendarAvailabilityResponse> selectAllAvailabilities() {
        List<CalendarAvailability> list = availabilityRepository.findAll();
        List<CalendarAvailabilityResponse> responses = new ArrayList<>();
        for (CalendarAvailability a : list) {
            responses.add(mapper.toDto(a));
        }
        return responses;
    }

    @Override
    public CalendarAvailabilityResponse updateAvailability(Long id, CalendarAvailabilityRequest request) {
        CalendarAvailability availability = availabilityRepository.findById(id).orElse(null);
        if (availability == null) return null;

        if (request.getCalendarId() != null) {
            ProviderCalendar calendar = providerCalendarRepository.findById(request.getCalendarId()).orElse(null);
            if (calendar == null) return null;
            availability.setCalendar(calendar);
        }

        if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        if (request.getMode() != null) availability.setMode(request.getMode());
        if (request.getStatus() != null) availability.setStatus(request.getStatus());
        if (request.getAddress() != null) availability.setAddress(request.getAddress());

        return mapper.toDto(availabilityRepository.save(availability));
    }

    @Override
    public void deleteAvailabilityById(Long id) {
        availabilityRepository.deleteById(id);
    }

    @Override
    public void deleteAllAvailabilities() {
        availabilityRepository.deleteAll();
    }

    @Override
    public long countingAvailabilities() {
        return availabilityRepository.count();
    }

    @Override
    public boolean verifAvailabilityById(Long id) {
        return availabilityRepository.existsById(id);
    }
}