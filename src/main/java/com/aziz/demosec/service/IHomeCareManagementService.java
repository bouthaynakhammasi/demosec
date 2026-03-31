package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.homecare.*;

import java.time.LocalDate;
import java.util.List;

public interface IHomeCareManagementService {

    // ── Catalogue ──────────────────────────────────────────────────────────
    List<HomeCareService> getAllActiveServices();

    List<ServiceProvider> getVerifiedProvidersByService(Long serviceId);

    // ── Recherche enrichie (Patient) ────────────────────────────────────────
    List<ProviderProfileDTO> searchProviders(Long serviceId, Double minRating);

    List<ProviderProfileDTO> searchAvailableProviders(Long serviceId, java.time.LocalDateTime dateTime);

    ProviderProfileDTO getProviderProfile(Long providerId);

    List<ServiceReview> getProviderReviews(Long providerId);

    List<AvailableSlotDTO> getProviderAvailableSlots(Long providerId, LocalDate from, LocalDate to);

    /**
     * ✅ Get blocked/unavailable dates for a provider on a date range
     * Returns a list of YYYY-MM-DD date strings where provider is marked
     * unavailable
     */
    List<String> getProviderBlockedDates(Long providerId, LocalDate from, LocalDate to);

    // ── Patient ────────────────────────────────────────────────────────────
    ServiceRequest createRequest(String patientEmail, CreateServiceRequestDTO dto);

    List<ServiceRequest> getMyRequests(String patientEmail);

    ServiceRequest getRequestById(Long requestId);

    void cancelRequest(Long requestId, String patientEmail);

    ServiceReview submitReview(Long requestId, String patientEmail, SubmitReviewDTO dto);

    ServiceRequest completeRequestAsPatient(Long requestId, String patientEmail);

    // ── Prestataire ────────────────────────────────────────────────────────
    List<ServiceRequest> getProviderRequests(String providerEmail);

    ServiceRequest acceptRequest(Long requestId, String providerEmail);

    ServiceRequest declineRequest(Long requestId, String providerEmail);

    ServiceRequest startRequest(Long requestId, String providerEmail);

    ServiceRequest completeRequest(Long requestId, String providerEmail, String providerNotes);

    List<ProviderAvailability> getMyAvailability(String providerEmail);

    ProviderAvailability saveAvailability(String providerEmail, AvailabilityDTO dto);

    void deleteAvailability(Long availabilityId, String providerEmail);

    ProviderAvailability blockDay(String providerEmail, LocalDate date);

    // ── Calendrier (Nouveau) ──────────────────────────────────────────────
    List<CalendarEventDTO> getProviderCalendarEvents(String providerEmail, LocalDate from, LocalDate to);

    // ── Admin ──────────────────────────────────────────────────────────────
    List<ServiceProvider> getAllProviders();

    List<ServiceProvider> getPendingProviders();

    ServiceProvider verifyProvider(Long providerId);

    void rejectProvider(Long providerId);

    List<ServiceRequest> getAllRequests();

    HomeCareService createService(HomeCareService service);

    HomeCareService updateService(Long id, HomeCareService updated);

    // ── Gestion des Spécialités (Affectation Complexe) ──────────────────────
    void addSpecialtyToProvider(Long providerId, Long serviceId);

    void removeSpecialtyFromProvider(Long providerId, Long serviceId);
}
