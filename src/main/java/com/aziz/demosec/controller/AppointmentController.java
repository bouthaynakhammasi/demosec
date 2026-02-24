package com.aziz.demosec.controller;

import com.aziz.demosec.dto.AppointmentRequest;
import com.aziz.demosec.dto.AppointmentResponse;
import com.aziz.demosec.service.IAppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private IAppointmentService appointmentService;

    @PostMapping("/add")
    public AppointmentResponse addAppointment(@RequestBody AppointmentRequest request) {
        return appointmentService.addAppointment(request);
    }

    @GetMapping("/get/{id}")
    public AppointmentResponse getAppointmentByIdWithGet(@PathVariable Long id) {
        return appointmentService.selectAppointmentByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public AppointmentResponse getAppointmentByIdWithOrElse(@PathVariable Long id) {
        return appointmentService.selectAppointmentByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.selectAllAppointments();
    }

    @PutMapping("/update/{id}")
    public AppointmentResponse updateAppointment(@PathVariable Long id,
                                                 @RequestBody AppointmentRequest request) {
        return appointmentService.updateAppointment(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAllAppointments() {
        appointmentService.deleteAllAppointments();
    }

    @GetMapping("/count")
    public long countAppointments() {
        return appointmentService.countingAppointments();
    }

    @GetMapping("/exists/{id}")
    public boolean existsAppointment(@PathVariable Long id) {
        return appointmentService.verifAppointmentById(id);
    }
}
