package com.aziz.demosec.service;

import com.aziz.demosec.dto.ProviderCalendarRequest;
import com.aziz.demosec.dto.ProviderCalendarResponse;

import java.util.List;

public interface IProviderCalendarService {

    ProviderCalendarResponse addCalendar(ProviderCalendarRequest request);

    ProviderCalendarResponse selectCalendarByIdWithGet(Long id);
    ProviderCalendarResponse selectCalendarByIdWithOrElse(Long id);

    List<ProviderCalendarResponse> selectAllCalendars();

    ProviderCalendarResponse updateCalendar(Long id, ProviderCalendarRequest request);

    void deleteCalendarById(Long id);
    void deleteAllCalendars();

    long countingCalendars();
    boolean verifCalendarById(Long id);
}