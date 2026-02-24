package com.aziz.demosec.service;


import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;

import java.util.List;

public interface IAppointmentService {
    AppointmentResponse addAppointment(AppointmentRequest request);

    AppointmentResponse selectAppointmentByIdWithGet(Long id);
    AppointmentResponse selectAppointmentByIdWithOrElse(Long id);

    List<AppointmentResponse> selectAllAppointments();

    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);

    void deleteAppointmentById(Long id);
    void deleteAllAppointments();

    long countingAppointments();
    boolean verifAppointmentById(Long id);
}
