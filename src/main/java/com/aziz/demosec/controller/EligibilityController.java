package com.aziz.demosec.controller;

import com.aziz.demosec.dto.ai.EligibilityResponseDTO;
import com.aziz.demosec.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class EligibilityController {

    private final EligibilityService eligibilityService;

    /**
     * POST /api/ai/eligibility/{aidRequestId}
     * Vérifie l'éligibilité du bénéficiaire lié à une demande d'aide.
     */
    @PostMapping("/eligibility/{aidRequestId}")
    public ResponseEntity<EligibilityResponseDTO> checkEligibility(
            @PathVariable Long aidRequestId) {
        try {
            EligibilityResponseDTO result = eligibilityService.checkEligibility(aidRequestId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Eligibility check error for #{}: {}", aidRequestId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/ai/eligibility/batch
     * Vérifie l'éligibilité pour plusieurs demandes d'aide en une seule requête.
     * Body: [1, 2, 3, ...]
     */
    @PostMapping("/eligibility/batch")
    public ResponseEntity<List<EligibilityResponseDTO>> checkBatch(
            @RequestBody List<Long> aidRequestIds) {
        return ResponseEntity.ok(eligibilityService.checkAll(aidRequestIds));
    }
}
