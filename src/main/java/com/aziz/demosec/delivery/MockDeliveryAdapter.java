package com.aziz.demosec.delivery;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mock implementation of DeliveryGateway for development/testing.
 * Replace with ColissimoAdapter or AnotherAgencyAdapter for production.
 * To switch: change @Primary or use Spring @Profile("prod").
 */
@Service
public class MockDeliveryAdapter implements DeliveryGateway {

    private static final String AGENCY_NAME = "MOCK";

    @Override
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        String trackingId = "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return DeliveryResponse.builder()
                .trackingId(trackingId)
                .agencyName(AGENCY_NAME)
                .trackingUrl("http://localhost:8080/api/pharmacy/delivery/mock/track/" + trackingId)
                .estimatedArrival(LocalDateTime.now().plusHours(2))
                .build();
    }
}
