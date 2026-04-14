package com.aziz.demosec.util;

/**
 * Utility class for GPS distance calculations.
 * Uses the Haversine formula — accurate within ~0.3% for short distances.
 */
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance in kilometers between two GPS coordinates.
     */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimate delivery time in minutes based on distance.
     * Uses 30 km/h average speed for urban Tunisia.
     * Adds 10 minutes preparation buffer.
     */
    public static int estimateDeliveryMinutes(double distanceKm) {
        int travelMinutes = (int) Math.ceil((distanceKm / 30.0) * 60);
        return travelMinutes + 10; // +10 min for preparation
    }

    private GeoUtils() {}
}
