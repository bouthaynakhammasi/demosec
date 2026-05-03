package com.aziz.demosec.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 🏥 Utility class for validating intervention dates for homecare bookings
 * 
 * Rules:
 * - Date must not be in the past
 * - Date must not be more than 90 days in the future
 * - Date must not be on provider's blocked/unavailable days (if provider
 * specified)
 * - Date must be valid and not null
 */
public class InterventionDateValidator {

    private static final int MAX_DAYS_AHEAD = 90;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Validates intervention date according to business rules (basic validation)
     * Throws IllegalArgumentException if validation fails
     *
     * @param requestedDateTime the intervention date to validate
     * @throws IllegalArgumentException if date is invalid
     */
    public static void validate(LocalDateTime requestedDateTime) {
        if (requestedDateTime == null) {
            throw new IllegalArgumentException("La date de l'intervention est obligatoire");
        }

        LocalDate requestedDate = requestedDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        // Check if date is in the past
        if (requestedDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "La date de l'intervention ne peut pas être dans le passé. " +
                            "Veuillez sélectionner une date à partir d'aujourd'hui.");
        }

        // Check if date exceeds maximum advance booking (90 days)
        LocalDate maxDate = today.plusDays(MAX_DAYS_AHEAD);
        if (requestedDate.isAfter(maxDate)) {
            throw new IllegalArgumentException(
                    "La date de l'intervention ne peut pas dépasser " + MAX_DAYS_AHEAD + " jours à l'avance. " +
                            "Veuillez choisir une date avant le " + maxDate.format(DATE_FORMATTER) + ".");
        }
    }

    /**
     * Validates intervention date with provider availability check
     * Call this when a specific provider is chosen
     *
     * @param requestedDateTime   the intervention date to validate
     * @param isProviderAvailable whether the provider is available on this date
     * @throws IllegalArgumentException if date is invalid or provider not available
     */
    public static void validateWithProviderAvailability(LocalDateTime requestedDateTime, boolean isProviderAvailable) {
        // First validate basic rules
        validate(requestedDateTime);

        // Then check provider availability
        if (!isProviderAvailable) {
            LocalDate requestedDate = requestedDateTime.toLocalDate();
            throw new IllegalArgumentException(
                    "Le prestataire sélectionné n'est pas disponible le " +
                            requestedDate.format(DATE_FORMATTER) + ". " +
                            "Veuillez choisir une autre date ou un autre prestataire.");
        }
    }

    /**
     * Validates intervention date and returns true if valid
     * Does not throw exceptions, used for checking without error throwing
     *
     * @param requestedDateTime the date to check
     * @return true if date is valid, false otherwise
     */
    public static boolean isValidInterventionDate(LocalDateTime requestedDateTime) {
        try {
            validate(requestedDateTime);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets the minimum allowed intervention date (today)
     * 
     * @return today's date
     */
    public static LocalDate getMinInterventionDate() {
        return LocalDate.now();
    }

    /**
     * Gets the maximum allowed intervention date (90 days from today)
     * 
     * @return maximum allowed date
     */
    public static LocalDate getMaxInterventionDate() {
        return LocalDate.now().plusDays(MAX_DAYS_AHEAD);
    }

    /**
     * Gets validation error message for a specific scenario
     * 
     * @param requestedDateTime the date that failed validation
     * @return French error message describing why validation failed
     */
    public static String getValidationErrorMessage(LocalDateTime requestedDateTime) {
        if (requestedDateTime == null) {
            return "La date de l'intervention est obligatoire";
        }

        LocalDate requestedDate = requestedDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        if (requestedDate.isBefore(today)) {
            return "La date de l'intervention ne peut pas être dans le passé.";
        }

        LocalDate maxDate = today.plusDays(MAX_DAYS_AHEAD);
        if (requestedDate.isAfter(maxDate)) {
            return "La date de l'intervention ne peut pas dépasser " + MAX_DAYS_AHEAD + " jours à l'avance.";
        }

        return "Date d'intervention invalide";
    }

    /**
     * Gets error message when provider is not available
     * 
     * @param providerId the provider ID
     * @param date       the date requested
     * @return French error message
     */
    public static String getProviderUnavailableMessage(Long providerId, LocalDate date) {
        return "Le prestataire (ID: " + providerId + ") n'est pas disponible le " +
                date.format(DATE_FORMATTER) + ".";
    }
}
