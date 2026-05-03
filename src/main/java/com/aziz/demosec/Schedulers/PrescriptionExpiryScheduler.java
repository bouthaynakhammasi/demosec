package com.aziz.demosec.Schedulers;

import com.aziz.demosec.Entities.Prescription;
import com.aziz.demosec.Entities.PrescriptionItem;
import com.aziz.demosec.Entities.PrescriptionStatus;
import com.aziz.demosec.repository.PrescriptionRepository;
import com.aziz.demosec.util.DurationParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Runs every day at 00:05 (5 min after TreatmentScheduler).
 *
 * Two passes:
 *
 * Pass 1 — Backfill expiryDate for prescriptions that don't have one yet.
 *           Iterates each prescription's items, parses their duration string,
 *           and stores max(prescription.date + duration) as expiryDate.
 *
 * Pass 2 — Mark ACTIVE prescriptions whose expiryDate < today as EXPIRED.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrescriptionExpiryScheduler {

    private final PrescriptionRepository prescriptionRepository;

    @Scheduled(cron = "0 5 0 * * *")   // 00:05 every day
    @Transactional
    public void expirePrescriptions() {
        LocalDate today = LocalDate.now();
        log.info("[PrescriptionScheduler] Running for date={}", today);

        // ── Pass 1: backfill expiryDate ──────────────────────────────────────
        List<Prescription> noExpiry = prescriptionRepository
                .findByStatusAndExpiryDateIsNull(PrescriptionStatus.ACTIVE);

        int backfilled = 0;
        for (Prescription p : noExpiry) {
            Optional<LocalDate> computed = computeExpiryDate(p);
            if (computed.isPresent()) {
                p.setExpiryDate(computed.get());
                backfilled++;
                log.debug("[PrescriptionScheduler] Backfilled expiryDate={} for prescription id={}",
                        computed.get(), p.getId());
            }
        }
        prescriptionRepository.saveAll(noExpiry);
        log.info("[PrescriptionScheduler] Pass 1 done. backfilled={}", backfilled);

        // ── Pass 2: mark expired ─────────────────────────────────────────────
        List<Prescription> expired = prescriptionRepository
                .findByStatusAndExpiryDateBefore(PrescriptionStatus.ACTIVE, today);

        expired.forEach(p -> {
            p.setStatus(PrescriptionStatus.EXPIRED);
            log.info("[PrescriptionScheduler] Expired prescription id={} patient={} expiryDate={}",
                    p.getId(),
                    p.getConsultation().getMedicalRecord().getPatient().getFullName(),
                    p.getExpiryDate());
        });
        prescriptionRepository.saveAll(expired);
        log.info("[PrescriptionScheduler] Pass 2 done. expired={}", expired.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Derive the prescription's overall expiry date from its items.
    // Logic: for each item, compute (prescription.date + item.duration).
    //        The prescription expires when the LAST item finishes → take max.
    // ─────────────────────────────────────────────────────────────────────────
    private Optional<LocalDate> computeExpiryDate(Prescription prescription) {
        return prescription.getItems().stream()
                .map(item -> resolveItemEnd(prescription.getDate(), item))
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder());
    }

    private Optional<LocalDate> resolveItemEnd(LocalDate prescriptionDate, PrescriptionItem item) {
        return DurationParser.resolveEndDate(prescriptionDate, item.getDuration());
    }
}