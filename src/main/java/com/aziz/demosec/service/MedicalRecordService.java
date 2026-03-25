package com.aziz.demosec.service;

import com.aziz.demosec.Entities.MedicalRecord;
import com.aziz.demosec.repository.MedicalRecordRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;
import com.aziz.demosec.Mapper.MedicalRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MedicalRecordService implements IMedicalRecordService {

    private MedicalRecordRepository medicalRecordRepository;
    private UserRepository userRepository;
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public MedicalRecordResponse addMedicalRecord(MedicalRecordRequest request) {

        User patient = userRepository.findById(request.getPatientId()).orElse(null);
        if (patient == null) return null;

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .build();

        return medicalRecordMapper.toDto(medicalRecordRepository.save(record));
    }

    @Override
    public MedicalRecordResponse selectMedicalRecordByIdWithGet(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id).get();
        return medicalRecordMapper.toDto(record);
    }

    @Override
    public MedicalRecordResponse selectMedicalRecordByIdWithOrElse(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id).orElse(null);
        if (record == null) return null;
        return medicalRecordMapper.toDto(record);
    }

    @Override
    public List<MedicalRecordResponse> selectAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findAll();
        List<MedicalRecordResponse> responses = new ArrayList<>();
        for (MedicalRecord r : records) {
            responses.add(medicalRecordMapper.toDto(r));
        }
        return responses;
    }

    @Override
    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id).orElse(null);
        if (record == null) return null;

        if (request.getPatientId() != null) {
            User patient = userRepository.findById(request.getPatientId()).orElse(null);
            if (patient == null) return null;
            record.setPatient(patient);
        }

        return medicalRecordMapper.toDto(medicalRecordRepository.save(record));
    }

    @Override
    public void deleteMedicalRecordById(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    @Override
    public void deleteAllMedicalRecords() {
        medicalRecordRepository.deleteAll();
    }

    @Override
    public long countingMedicalRecords() {
        return medicalRecordRepository.count();
    }

    @Override
    public boolean verifMedicalRecordById(Long id) {
        return medicalRecordRepository.existsById(id);
    }

    @Override
    public MedicalRecordResponse selectMedicalRecordByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .map(medicalRecordMapper::toDto)
                .orElse(null);
    }
}