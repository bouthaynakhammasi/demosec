package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.SmartDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmartDeviceRepository extends JpaRepository<SmartDevice, Long> {
    Optional<SmartDevice> findByPatientId(Long patientId);
    boolean existsByPatientId(Long patientId);
}
