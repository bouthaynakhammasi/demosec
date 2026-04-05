package com.aziz.demosec.Mapper;

import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.Entities.appointment.Appointment;
import com.aziz.demosec.Entities.appointment.Mode;
import com.aziz.demosec.dto.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        User doctorUser = appointment.getDoctor();
        String specialty = null;
        String clinicName = null;
        String clinicAddress = null;

        if (doctorUser instanceof Doctor) {
            Doctor d = (Doctor) doctorUser;
            specialty = d.getSpecialty();
            if (d.getClinic() != null) {
                clinicName = d.getClinic().getName();
                clinicAddress = d.getClinic().getAddress();
            }
        }

        String dateStr = appointment.getStartTime() != null ? appointment.getStartTime().toLocalDate().toString() : null;
        String startStr = appointment.getStartTime() != null ? appointment.getStartTime().toLocalTime().toString().substring(0, 5) : null;
        String endStr = appointment.getEndTime() != null ? appointment.getEndTime().toLocalTime().toString().substring(0, 5) : null;

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatient() != null ? appointment.getPatient().getFullName() : null)
                .providerId(appointment.getDoctorId())
                .providerName(appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : null)
                .dateTime(appointment.getStartTime())
                .date(dateStr)
                .startTime(startStr)
                .endTime(endStr)
                .mode(appointment.getVisitAddress() != null ? Mode.IN_PERSON : Mode.ONLINE)
                .status(appointment.getStatus())
                .meetingLink(appointment.getMeetingLink())
                .visitAddress(appointment.getVisitAddress())
                .patientNotes(appointment.getPatientNotes())
                .providerNotes(appointment.getProviderNotes())
                .doctorSpecialty(specialty)
                .clinicName(clinicName)
                .clinicAddress(clinicAddress)
                .cancelledAt(appointment.getCancelledAt())
                .completedAt(appointment.getCompletedAt())
                .build();
    }

    public Appointment toEntity(AppointmentResponse dto) {
        if (dto == null) {
            return null;
        }
        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setStatus(dto.getStatus());
        appointment.setMeetingLink(dto.getMeetingLink());
        appointment.setVisitAddress(dto.getVisitAddress());
        appointment.setPatientNotes(dto.getPatientNotes());
        appointment.setProviderNotes(dto.getProviderNotes());
        appointment.setCancelledAt(dto.getCancelledAt());
        appointment.setCompletedAt(dto.getCompletedAt());
        return appointment;
    }
}
