// EmergencyService.java
package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.emergency.*;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyServiceImpl implements IEmergencyService {

    private final SmartDeviceRepository smartDeviceRepository;
    private final EmergencyAlertRepository alertRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final EmergencyInterventionRepository interventionRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    // ─── SMART DEVICE ─────────────────────────────────────────────

    @Override
    public SmartDeviceResponseDTO createSmartDevice(SmartDeviceRequestDTO dto) {
        if (smartDeviceRepository.existsByPatientId(dto.getPatientId())) {
            throw new RuntimeException("Patient already has a SmartDevice");
        }
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        SmartDevice device = SmartDevice.builder()
                .patient(patient)
                .build();

        return toSmartDeviceDTO(smartDeviceRepository.save(device));
    }

    @Override
    public SmartDeviceResponseDTO getSmartDeviceById(Long id) {
        return toSmartDeviceDTO(findDeviceById(id));
    }

    @Override
    public List<SmartDeviceResponseDTO> getAllSmartDevices() {
        return smartDeviceRepository.findAll()
                .stream().map(this::toSmartDeviceDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSmartDevice(Long id) {
        smartDeviceRepository.deleteById(id);
    }

    // ─── EMERGENCY ALERT ──────────────────────────────────────────

    @Override
    public EmergencyAlertResponseDTO createAlert(EmergencyAlertRequestDTO dto) {
        SmartDevice device = findDeviceById(dto.getSmartDeviceId());

        EmergencyAlert alert = EmergencyAlert.builder()
                .device(device)
                .severity(dto.getSeverity())
                .status(EmergencyAlertStatus.PENDING)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .canceledByPatient(false)
                .build();

        return toAlertDTO(alertRepository.save(alert));
    }

    @Override
    public EmergencyAlertResponseDTO getAlertById(Long id) {
        return toAlertDTO(findAlertById(id));
    }

    @Override
    public List<EmergencyAlertResponseDTO> getAllAlerts() {
        return alertRepository.findAll()
                .stream().map(this::toAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyAlertResponseDTO> getAlertsByStatus(EmergencyAlertStatus status) {
        return alertRepository.findByStatus(status)
                .stream().map(this::toAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyAlertResponseDTO updateAlertStatus(Long id, EmergencyAlertStatus status) {
        EmergencyAlert alert = findAlertById(id);
        alert.setStatus(status);
        return toAlertDTO(alertRepository.save(alert));
    }

    @Override
    public EmergencyAlertResponseDTO cancelAlertByPatient(Long id) {
        EmergencyAlert alert = findAlertById(id);
        alert.setCanceledByPatient(true);
        alert.setStatus(EmergencyAlertStatus.RESOLVED);
        return toAlertDTO(alertRepository.save(alert));
    }

    // ─── AMBULANCE ────────────────────────────────────────────────

    @Override
    public AmbulanceResponseDTO createAmbulance(AmbulanceRequestDTO dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseGet(() -> {
                    // Fallback to the first clinic if not found by ID (useful for dev/env issues)
                    return clinicRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No clinics found in database. Please create a clinic first."));
                });

        Ambulance ambulance = Ambulance.builder()
                .clinic(clinic)
                .currentLat(dto.getCurrentLat())
                .currentLng(dto.getCurrentLng())
                .licensePlate(dto.getLicensePlate())
                .status(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE")
                .build();

        return toAmbulanceDTO(ambulanceRepository.save(ambulance));
    }

    @Override
    public AmbulanceResponseDTO getAmbulanceById(Long id) {
        return toAmbulanceDTO(findAmbulanceById(id));
    }

    @Override
    public List<AmbulanceResponseDTO> getAllAmbulances() {
        return ambulanceRepository.findAll()
                .stream().map(this::toAmbulanceDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AmbulanceResponseDTO> getAmbulancesByClinic(Long clinicId) {
        return ambulanceRepository.findByClinicId(clinicId)
                .stream().map(this::toAmbulanceDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AmbulanceResponseDTO updateAmbulance(Long id, AmbulanceRequestDTO dto) {
        Ambulance ambulance = findAmbulanceById(id);
        ambulance.setCurrentLat(dto.getCurrentLat());
        ambulance.setCurrentLng(dto.getCurrentLng());
        if (dto.getLicensePlate() != null) {
            ambulance.setLicensePlate(dto.getLicensePlate());
        }
        if (dto.getStatus() != null) {
            ambulance.setStatus(dto.getStatus());
        }
        return toAmbulanceDTO(ambulanceRepository.save(ambulance));
    }

    @Override
    public void deleteAmbulance(Long id) {
        ambulanceRepository.deleteById(id);
    }

    // ─── INTERVENTION ─────────────────────────────────────────────

    @Override
    public EmergencyInterventionResponseDTO dispatchIntervention(EmergencyInterventionRequestDTO dto) {
        EmergencyAlert alert = findAlertById(dto.getEmergencyAlertId());
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        Ambulance ambulance = findAmbulanceById(dto.getAmbulanceId());

        // Mettre à jour le statut de l'alerte
        alert.setStatus(EmergencyAlertStatus.CLINIC_NOTIFIED);
        alertRepository.save(alert);

        EmergencyIntervention intervention = EmergencyIntervention.builder()
                .emergencyAlert(alert)
                .clinic(clinic)
                .ambulance(ambulance)
                .dispatchedAt(LocalDateTime.now())
                .status(EmergencyInterventionStatus.DISPATCHED)
                .build();

        return toInterventionDTO(interventionRepository.save(intervention));
    }

    @Override
    public EmergencyInterventionResponseDTO getInterventionById(Long id) {
        return toInterventionDTO(findInterventionById(id));
    }

    @Override
    public List<EmergencyInterventionResponseDTO> getAllInterventions() {
        return interventionRepository.findAll()
                .stream().map(this::toInterventionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyInterventionResponseDTO updateInterventionStatus(Long id, EmergencyInterventionStatus status) {
        EmergencyIntervention intervention = findInterventionById(id);
        intervention.setStatus(status);

        if (status == EmergencyInterventionStatus.ARRIVED) {
            intervention.setArrivedAt(LocalDateTime.now());
        }
        if (status == EmergencyInterventionStatus.COMPLETED) {
            intervention.setCompletedAt(LocalDateTime.now());
        }

        return toInterventionDTO(interventionRepository.save(intervention));
    }

    // ─── HELPERS PRIVÉS ───────────────────────────────────────────

    private SmartDevice findDeviceById(Long id) {
        return smartDeviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SmartDevice not found: " + id));
    }

    private EmergencyAlert findAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
    }

    private Ambulance findAmbulanceById(Long id) {
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambulance not found: " + id));
    }

    private EmergencyIntervention findInterventionById(Long id) {
        return interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention not found: " + id));
    }

    // ─── MAPPERS ──────────────────────────────────────────────────

    private SmartDeviceResponseDTO toSmartDeviceDTO(SmartDevice d) {
        return SmartDeviceResponseDTO.builder()
                .id(d.getId())
                .patientId(d.getPatient().getId())
                .patientName(d.getPatient().getFullName())
                .build();
    }

    private EmergencyAlertResponseDTO toAlertDTO(EmergencyAlert a) {
        return EmergencyAlertResponseDTO.builder()
                .id(a.getId())
                .smartDeviceId(a.getDevice().getId())
                .patientName(a.getDevice().getPatient().getFullName())
                .severity(a.getSeverity())
                .status(a.getStatus())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .canceledByPatient(a.getCanceledByPatient())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private AmbulanceResponseDTO toAmbulanceDTO(Ambulance a) {
        return AmbulanceResponseDTO.builder()
                .id(a.getId())
                .clinicId(a.getClinic() != null ? a.getClinic().getId() : null)
                .currentLat(a.getCurrentLat())
                .currentLng(a.getCurrentLng())
                .licensePlate(a.getLicensePlate())
                .status(a.getStatus() != null ? a.getStatus() : "AVAILABLE")
                .build();
    }

    private EmergencyInterventionResponseDTO toInterventionDTO(EmergencyIntervention i) {
        return EmergencyInterventionResponseDTO.builder()
                .id(i.getId())
                .emergencyAlertId(i.getEmergencyAlert().getId())
                .clinicId(i.getClinic().getId())
                .ambulanceId(i.getAmbulance().getId())
                .patientName(i.getEmergencyAlert().getDevice().getPatient().getFullName())
                .status(i.getStatus())
                .dispatchedAt(i.getDispatchedAt())
                .arrivedAt(i.getArrivedAt())
                .completedAt(i.getCompletedAt())
                .build();
    }
}