package com.aziz.demosec.service;

import com.aziz.demosec.entities.*;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final StockBatchRepository stockBatchRepository;
    private final PharmacyStockRepository pharmacyStockRepository;
    private final StockAlertRepository stockAlertRepository;
    private final MedicalEventRepository medicalEventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    /**
     * Stock Module: Every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkStockAlerts() {
        log.info("Starting scheduled stock check at {}", LocalDateTime.now());

        // 1. Check for expiring batches (within 7 days)
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        List<StockBatch> expiringBatches = stockBatchRepository.findByExpirationDateLessThanEqual(sevenDaysFromNow);
        
        for (StockBatch batch : expiringBatches) {
            if (batch.getExpirationDate().isAfter(LocalDate.now())) {
                createAlert(batch.getPharmacyStock(), StockAlertType.EXPIRING_SOON, 
                    "Batch " + batch.getBatchNumber() + " for product " + 
                    batch.getPharmacyStock().getProduct().getName() + " expires on " + batch.getExpirationDate());
            } else {
                createAlert(batch.getPharmacyStock(), StockAlertType.EXPIRED, 
                    "Batch " + batch.getBatchNumber() + " for product " + 
                    batch.getPharmacyStock().getProduct().getName() + " has EXPIRED");
            }
        }

        // 2. Check for Low Stock and Out of Stock
        List<PharmacyStock> allStocks = pharmacyStockRepository.findAll();
        for (PharmacyStock stock : allStocks) {
            if (stock.getTotalQuantity() == 0) {
                createAlert(stock, StockAlertType.OUT_OF_STOCK, 
                    "Product " + stock.getProduct().getName() + " is OUT OF STOCK");
            } else if (stock.getTotalQuantity() < stock.getMinQuantityThreshold()) {
                createAlert(stock, StockAlertType.LOW_STOCK, 
                    "Product " + stock.getProduct().getName() + " is LOW ON STOCK (" + stock.getTotalQuantity() + ")");
            }
        }
    }

    private void createAlert(PharmacyStock stock, StockAlertType type, String message) {
        // Check if an unresolved alert of the same type already exists for this stock
        boolean exists = stockAlertRepository.existsByPharmacyStockIdAndAlertTypeAndResolvedFalse(stock.getId(), type);
        if (!exists) {
            StockAlert alert = StockAlert.builder()
                .pharmacyStock(stock)
                .alertType(type)
                .message(message)
                .createdAt(LocalDateTime.now())
                .resolved(false)
                .build();
            stockAlertRepository.save(alert);
            log.info("Created {} alert for product {}", type, stock.getProduct().getName());
        }
    }

    /**
     * Event Module: Every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void processEventWaitlists() {
        log.info("Starting scheduled event waitlist processing at {}", LocalDateTime.now());

        List<MedicalEvent> activeEvents = medicalEventRepository.findAll(); // Could filter for upcoming events

        for (MedicalEvent event : activeEvents) {
            if (event.getCapacity() != null && event.getCapacity() > 0) {
                // Count currently REGISTERED/VALIDATED users
                long registeredCount = eventRegistrationRepository.findByEventIdAndStatus(event.getId(), RegistrationStatus.REGISTERED).size() +
                                     eventRegistrationRepository.findByEventIdAndStatus(event.getId(), RegistrationStatus.VALIDATED).size();

                int availableSpots = (int) (event.getCapacity() - registeredCount);

                if (availableSpots > 0) {
                    // Get users from waitlist in order
                    List<EventRegistration> waitlisted = eventRegistrationRepository.findByEventIdAndStatusOrderByCreatedAtAsc(event.getId(), RegistrationStatus.WAITLISTED);
                    
                    int toPromote = Math.min(availableSpots, waitlisted.size());
                    for (int i = 0; i < toPromote; i++) {
                        EventRegistration reg = waitlisted.get(i);
                        reg.setStatus(RegistrationStatus.REGISTERED);
                        eventRegistrationRepository.save(reg);
                        log.info("Promoted user {} to REGISTERED for event {}", reg.getParticipant().getEmail(), event.getTitle());
                    }
                }
            }
        }
    }
}
