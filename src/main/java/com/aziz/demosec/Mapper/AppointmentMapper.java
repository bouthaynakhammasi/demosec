package com.aziz.demosec.Mapper;


import com.aziz.demosec.Entities.Appointment;
import com.aziz.demosec.dto.AppointmentResponse;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(
                        appointment.getPatient() != null
                                ? appointment.getPatient().getId()
                                : null
                )
                .providerId(
                        appointment.getProvider() != null
                                ? appointment.getProvider().getId()
                                : null
                )
                .availabilityId(
                        appointment.getAvailability() != null
                                ? appointment.getAvailability().getId()
                                : null
                )
                .status(appointment.getStatus())
                .mode(appointment.getMode())
                .meetingLink(appointment.getMeetingLink())
                .visitAddress(appointment.getVisitAddress())
                .build();
    }

    public Appointment toEntity(AppointmentResponse dto) {
        if (dto == null) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setStatus(dto.getStatus());
        appointment.setMode(dto.getMode());
        appointment.setMeetingLink(dto.getMeetingLink());
        appointment.setVisitAddress(dto.getVisitAddress());


        return appointment;
    }
}