package com.aziz.demosec.controller;

import com.aziz.demosec.dto.MedicalRecordRequest;
import com.aziz.demosec.dto.MedicalRecordResponse;
import com.aziz.demosec.service.IMedicalRecordService;
import com.aziz.demosec.service.MedicalRecordSummaryService;
import com.aziz.demosec.service.VoiceRssService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/medical-record")
public class MedicalRecordController {

    private IMedicalRecordService medicalRecordService;

    @Autowired
    private MedicalRecordSummaryService summaryService;
    @Autowired private VoiceRssService voiceRssService;

    @PostMapping("/add")
    public MedicalRecordResponse addMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        return medicalRecordService.addMedicalRecord(request);
    }

    @GetMapping("/get/{id}")
    public MedicalRecordResponse getByIdWithGet(@PathVariable Long id) {
        return medicalRecordService.selectMedicalRecordByIdWithGet(id);
    }

    @GetMapping("/get-or-else/{id}")
    public MedicalRecordResponse getByIdWithOrElse(@PathVariable Long id) {
        return medicalRecordService.selectMedicalRecordByIdWithOrElse(id);
    }

    @GetMapping("/all")
    public List<MedicalRecordResponse> getAll() {
        return medicalRecordService.selectAllMedicalRecords();
    }

    @PutMapping("/update/{id}")
    public MedicalRecordResponse update(@PathVariable Long id, @Valid @RequestBody MedicalRecordRequest request) {
        return medicalRecordService.updateMedicalRecord(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecordById(id);
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        medicalRecordService.deleteAllMedicalRecords();
    }

    @GetMapping("/count")
    public long count() {
        return medicalRecordService.countingMedicalRecords();
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return medicalRecordService.verifMedicalRecordById(id);
    }

    @GetMapping("/patient/{patientId}")
    public MedicalRecordResponse getByPatientId(@PathVariable Long patientId) {
        return medicalRecordService.selectMedicalRecordByPatientId(patientId);
    }

    // Returns just the text summary
    @GetMapping("/{recordId}/summarize")
    public ResponseEntity<Map<String, String>> summarize(@PathVariable Long recordId) {
        String summary = summaryService.getSummary(recordId);
        return ResponseEntity.ok(Map.of("summary", summary));
    }

    // Returns audio directly
    @GetMapping(value = "/{recordId}/speak", produces = "audio/mpeg")
    public ResponseEntity<?> speak(@PathVariable Long recordId) {
        try {
            String summary = summaryService.getSummary(recordId);
            byte[] audio = voiceRssService.convertTextToSpeech(summary);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.set("Content-Disposition", "inline");

            return new ResponseEntity<>(audio, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}