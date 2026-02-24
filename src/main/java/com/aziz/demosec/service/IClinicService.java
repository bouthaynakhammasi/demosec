package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Clinic;

import java.util.List;
import java.util.Optional;

public interface IClinicService {

    List<Clinic> getAllClinics();

    Optional<Clinic> getClinicById(Long id);

    Clinic createClinic(Clinic clinic);

    Clinic updateClinic(Long id, Clinic clinicDetails);

    void deleteClinic(Long id);
}