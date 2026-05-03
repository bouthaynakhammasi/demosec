package com.aziz.demosec.service;

import com.aziz.demosec.Entities.ProviderCalendar;
import com.aziz.demosec.Mapper.ProviderCalendarMapper;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.ProviderCalendarRequest;
import com.aziz.demosec.dto.ProviderCalendarResponse;
import com.aziz.demosec.repository.ProviderCalendarRepository;
import com.aziz.demosec.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderCalendarServiceTest {

    @Mock
    private ProviderCalendarRepository calendarRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProviderCalendarMapper mapper;

    @InjectMocks
    private ProviderCalendarService providerCalendarService;

    private User provider;
    private ProviderCalendar calendar;
    private ProviderCalendarRequest request;
    private ProviderCalendarResponse response;

    @BeforeEach
    void setUp() {
        provider = new User();
        provider.setId(1L);
        provider.setFullName("Dr. Test");

        calendar = ProviderCalendar.builder()
                .id(1L)
                .provider(provider)
                .build();

        request = new ProviderCalendarRequest();
        request.setProviderId(1L);

        response = new ProviderCalendarResponse();
        response.setId(1L);
        response.setProviderId(1L);
    }

    @Test
    void addCalendar_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(calendarRepository.save(any(ProviderCalendar.class))).thenReturn(calendar);
        when(mapper.toDto(any(ProviderCalendar.class))).thenReturn(response);

        ProviderCalendarResponse result = providerCalendarService.addCalendar(request);

        assertNotNull(result);
        assertEquals(1L, result.getProviderId());
    }

    @Test
    void addCalendar_ProviderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ProviderCalendarResponse result = providerCalendarService.addCalendar(request);

        assertNull(result);
    }

    @Test
    void selectCalendarByIdWithGet_Success() {
        when(calendarRepository.findById(1L)).thenReturn(Optional.of(calendar));
        when(mapper.toDto(calendar)).thenReturn(response);

        ProviderCalendarResponse result = providerCalendarService.selectCalendarByIdWithGet(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void selectCalendarByIdWithOrElse_Success() {
        when(calendarRepository.findById(1L)).thenReturn(Optional.of(calendar));
        when(mapper.toDto(calendar)).thenReturn(response);

        ProviderCalendarResponse result = providerCalendarService.selectCalendarByIdWithOrElse(1L);

        assertNotNull(result);
    }

    @Test
    void selectCalendarByIdWithOrElse_NotFound() {
        when(calendarRepository.findById(1L)).thenReturn(Optional.empty());

        ProviderCalendarResponse result = providerCalendarService.selectCalendarByIdWithOrElse(1L);

        assertNull(result);
    }

    @Test
    void selectAllCalendars_Success() {
        when(calendarRepository.findAll()).thenReturn(List.of(calendar));
        when(mapper.toDto(calendar)).thenReturn(response);

        List<ProviderCalendarResponse> results = providerCalendarService.selectAllCalendars();

        assertEquals(1, results.size());
    }

    @Test
    void updateCalendar_Success() {
        when(calendarRepository.findById(1L)).thenReturn(Optional.of(calendar));
        when(userRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(calendarRepository.save(any(ProviderCalendar.class))).thenReturn(calendar);
        when(mapper.toDto(calendar)).thenReturn(response);

        ProviderCalendarResponse result = providerCalendarService.updateCalendar(1L, request);

        assertNotNull(result);
    }

    @Test
    void deleteCalendarById_Success() {
        doNothing().when(calendarRepository).deleteById(1L);

        providerCalendarService.deleteCalendarById(1L);

        verify(calendarRepository, times(1)).deleteById(1L);
    }

    @Test
    void countingCalendars_Success() {
        when(calendarRepository.count()).thenReturn(5L);

        long count = providerCalendarService.countingCalendars();

        assertEquals(5, count);
    }

    @Test
    void verifCalendarById_Success() {
        when(calendarRepository.existsById(1L)).thenReturn(true);

        boolean exists = providerCalendarService.verifCalendarById(1L);

        assertTrue(exists);
    }
}
