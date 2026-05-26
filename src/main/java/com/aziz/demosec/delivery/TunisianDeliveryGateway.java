package com.aziz.demosec.delivery;

import com.aziz.demosec.util.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simulates a Tunisian delivery agency (DélivExpress TN).
 * Produces realistic tracking IDs, URLs, and ETA using Haversine.
 *
 * In production, replace this class with a real HTTP client
 * (e.g., RestTemplate calling the agency's REST API).
 */
@Component
@Primary
@Slf4j
public class TunisianDeliveryGateway implements DeliveryGateway {

    private static final String AGENCY_NAME = "DélivExpress TN";
    private static final String BASE_TRACKING_URL = "https://delivexpress.tn/track/";

    // Default pharmacy coordinates (Tunis center) — override via application.properties
    @Value("${pharmacy.default.lat:36.8065}")
    private double defaultPharmacyLat;

    @Value("${pharmacy.default.lng:10.1815}")
    private double defaultPharmacyLng;

    @Override
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        String trackingId = generateTrackingId(request.getExternalOrderRef());

        // Use Haversine for ETA — defaults to 25 min if coords not available
        int etaMinutes = estimateEta(request);
        LocalDateTime eta = LocalDateTime.now().plusMinutes(etaMinutes);

        log.info("[DélivExpress TN] New delivery created:");
        log.info("  Order Ref    : {}", request.getExternalOrderRef());
        log.info("  Pickup       : {}", request.getPickupAddress());
        log.info("  Dropoff      : {}", request.getDropoffAddress());
        log.info("  Tracking ID  : {}", trackingId);
        log.info("  ETA          : {} min ({})", etaMinutes, eta);

        return DeliveryResponse.builder()
                .trackingId(trackingId)
                .trackingUrl(BASE_TRACKING_URL + trackingId)
                .estimatedArrival(eta)
                .agencyName(AGENCY_NAME)
                .build();
    }

    private String generateTrackingId(String orderRef) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String paddedRef = String.format("%04d", safeParseId(orderRef));
        return "DLVR-TN-" + date + "-" + paddedRef;
    }

    private int estimateEta(DeliveryRequest request) {
        if (request.getPickupLat() != null && request.getDropoffLat() != null) {
            double distanceKm = GeoUtils.haversine(
                    request.getPickupLat(), request.getPickupLng(),
                    request.getDropoffLat(), request.getDropoffLng()
            );
            return GeoUtils.estimateDeliveryMinutes(distanceKm);
        }
        return 25; // default ETA in minutes for Tunisia urban areas
    }

    private long safeParseId(String ref) {
        try { return Long.parseLong(ref); } catch (Exception e) { return 0L; }
    }
}
