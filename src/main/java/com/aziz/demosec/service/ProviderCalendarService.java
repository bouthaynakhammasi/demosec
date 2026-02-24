package com.aziz.demosec.service;

import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.repository.ProviderCalendarRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.ProviderCalendarRequest;
import com.aziz.demosec.dto.ProviderCalendarResponse;
import com.aziz.demosec.Mapper.ProviderCalendarMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProviderCalendarService implements IProviderCalendarService {

    private ProviderCalendarRepository calendarRepository;
    private UserRepository userRepository;
    private ProviderCalendarMapper mapper;

    @Override
    public ProviderCalendarResponse addCalendar(ProviderCalendarRequest request) {

        User provider = userRepository.findById(request.getProviderId()).orElse(null);
        if (provider == null) return null;

        ProviderCalendar calendar = ProviderCalendar.builder()
                .provider(provider)
                .build();

        return mapper.toDto(calendarRepository.save(calendar));
    }

    @Override
    public ProviderCalendarResponse selectCalendarByIdWithGet(Long id) {
        ProviderCalendar calendar = calendarRepository.findById(id).get();
        return mapper.toDto(calendar);
    }

    @Override
    public ProviderCalendarResponse selectCalendarByIdWithOrElse(Long id) {
        ProviderCalendar calendar = calendarRepository.findById(id).orElse(null);
        if (calendar == null) return null;
        return mapper.toDto(calendar);
    }

    @Override
    public List<ProviderCalendarResponse> selectAllCalendars() {
        List<ProviderCalendar> calendars = calendarRepository.findAll();
        List<ProviderCalendarResponse> responses = new ArrayList<>();
        for (ProviderCalendar c : calendars) {
            responses.add(mapper.toDto(c));
        }
        return responses;
    }

    @Override
    public ProviderCalendarResponse updateCalendar(Long id, ProviderCalendarRequest request) {
        ProviderCalendar calendar = calendarRepository.findById(id).orElse(null);
        if (calendar == null) return null;

        if (request.getProviderId() != null) {
            User provider = userRepository.findById(request.getProviderId()).orElse(null);
            if (provider == null) return null;
            calendar.setProvider(provider);
        }

        return mapper.toDto(calendarRepository.save(calendar));
    }

    @Override
    public void deleteCalendarById(Long id) {
        calendarRepository.deleteById(id);
    }

    @Override
    public void deleteAllCalendars() {
        calendarRepository.deleteAll();
    }

    @Override
    public long countingCalendars() {
        return calendarRepository.count();
    }

    @Override
    public boolean verifCalendarById(Long id) {
        return calendarRepository.existsById(id);
    }
}