package com.aziz.demosec.service;



import com.aziz.demosec.dto.patient.PatientRequestDTO;
import com.aziz.demosec.dto.patient.PatientResponseDTO;

import java.util.List;

public interface IPatientService {
    PatientResponseDTO create(PatientRequestDTO dto);
    PatientResponseDTO getById(Long id);
    List<PatientResponseDTO> getAll();
    PatientResponseDTO update(Long id, PatientRequestDTO dto);
    void delete(Long id);
    void toggleEnabled(Long id);
}
