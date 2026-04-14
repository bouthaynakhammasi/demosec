package com.aziz.demosec.service;

import com.aziz.demosec.repository.AidRequestRepository;
import com.aziz.demosec.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Nightly scheduler for the donation module.
 *
 * Runs at midnight every day (00:00:00 server time):
 *   1. Expire AVAILABLE donations older than 30 days → EXPIRED
 *   2. Auto-reject PENDING AidRequests older than 7 days → REJECTED
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DonationScheduler {

    private static final int DONATION_EXPIRY_DAYS   = 30;
    private static final int AID_REQUEST_REJECT_DAYS = 7;

    private final DonationRepository    donationRepository;
    private final AidRequestRepository  aidRequestRepository;

    /**
     * Expires stale AVAILABLE donations (older than 30 days).
     * Runs every night at midnight.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void expireOldDonations() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(DONATION_EXPIRY_DAYS);
        int count = donationRepository.expireOldDonations(cutoff);
        log.info("[DonationScheduler] Expired {} donation(s) older than {} days.", count, DONATION_EXPIRY_DAYS);
    }

    /**
     * Auto-rejects stale PENDING aid requests (older than 7 days).
     * Runs every night at midnight.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void rejectOldAidRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(AID_REQUEST_REJECT_DAYS);
        int count = aidRequestRepository.rejectOldAidRequests(cutoff);
        log.info("[DonationScheduler] Rejected {} aid request(s) older than {} days.", count, AID_REQUEST_REJECT_DAYS);
    }
}
