package com.aziz.demosec.Schedulers;

import com.aziz.demosec.Entities.Treatment;
import com.aziz.demosec.Entities.TreatmentStatus;
import com.aziz.demosec.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler: Runs daily at midnight.
 * Business logic:
 *   1. Auto-complete treatments whose endDate has passed but status is still IN_PROGRESS.
 *   2. Cancel treatments whose startDate is still in the future but endDate has already passed
 *      (data integrity guard — e.g., bad data entry).
 *   3. Log a summary of what was updated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TreatmentScheduler {

    private final TreatmentRepository treatmentRepository;

    @Scheduled(cron = "0 0 0 * * *")   // every day at 00:00:00
    @Transactional
    public void autoCloseTreatments() {
        LocalDate today = LocalDate.now();
        log.info("[Scheduler] Running autoCloseTreatments for date={}", today);

        // 1. Auto-complete: endDate < today AND status = IN_PROGRESS
        List<Treatment> expired = treatmentRepository
                .findByStatusAndEndDateBefore(TreatmentStatus.ONGOING, today);

        expired.forEach(t -> {
            t.setStatus(TreatmentStatus.COMPLETED);
            log.info("[Scheduler] Auto-completed treatment id={} patient={} endDate={}",
                    t.getId(),
                    t.getConsultation().getMedicalRecord().getPatient().getFullName(),
                    t.getEndDate());
        });
        treatmentRepository.saveAll(expired);

        // 2. Cancel orphaned: startDate > today AND endDate < today (bad data)
        List<Treatment> orphaned = treatmentRepository
                .findByStatusAndStartDateAfterAndEndDateBefore(
                        TreatmentStatus.ONGOING, today, today);

        orphaned.forEach(t -> {
            t.setStatus(TreatmentStatus.CANCELLED);
            log.warn("[Scheduler] Cancelled orphaned treatment id={}", t.getId());
        });
        treatmentRepository.saveAll(orphaned);

        log.info("[Scheduler] Done. auto-completed={} cancelled-orphaned={}",
                expired.size(), orphaned.size());
    }
}