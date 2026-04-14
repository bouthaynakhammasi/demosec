package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.homecare.*;
import com.aziz.demosec.dto.pharmacy.NotificationResponseDTO;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeCareManagementServiceImpl implements HomeCareManagementService {

    private final HomeCareServiceRepository homeCareServiceRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ProviderAvailabilityRepository providerAvailabilityRepository;
    private final ServiceReviewRepository serviceReviewRepository;
    private final com.aziz.demosec.repository.UserRepository userRepository;
    private final com.aziz.demosec.repository.PatientRepository patientRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Catalogue ──────────────────────────────────────────────────────────

    @Override
    public List<HomeCareService> getAllActiveServices() {
        return homeCareServiceRepository.findByActiveTrue();
    }

    @Override
    public List<ServiceProvider> getVerifiedProvidersByService(Long serviceId) {
        return serviceProviderRepository.findVerifiedByServiceId(serviceId);
    }

    // ── Recherche enrichie (Patient) ────────────────────────────────────────

    @Override
    public List<ProviderProfileDTO> searchProviders(Long serviceId, Double minRating) {
        List<ServiceProvider> providers = getVerifiedProvidersByService(serviceId);
        return providers.stream()
                .filter(p -> minRating == null || p.getAverageRating() >= minRating)
                .map(this::toProviderProfileDTO) // Assuming convertToProfileDTO is a typo and should be
                                                 // toProviderProfileDTO
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderProfileDTO> searchAvailableProviders(Long serviceId, LocalDateTime dateTime) {
        HomeCareService service = homeCareServiceRepository.findById(serviceId).orElse(null);
        int duration = (service != null && service.getDurationMinutes() != null) ? service.getDurationMinutes() : 60;

        List<ServiceProvider> providers = getVerifiedProvidersByService(serviceId);
        return providers.stream()
                .filter(p -> {
                    // Si on cherche précisément à minuit (date seule choisie sur le front),
                    // on vérifie s'il y a AU MOINS un créneau ce jour là.
                    if (dateTime.toLocalTime().equals(LocalTime.MIN)
                            || dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                        return !getProviderAvailableSlots(p.getId(), dateTime.toLocalDate(), dateTime.toLocalDate())
                                .isEmpty();
                    }
                    // Sinon on vérifie précisément le créneau demandé
                    return isProviderAvailableFor(p.getId(), dateTime, duration);
                })
                .map(this::toProviderProfileDTO)
                .collect(Collectors.toList());
    }

    private boolean isProviderAvailableFor(Long providerId, LocalDateTime startDateTime, int durationMinutes) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        // On vérifie par tranches de 15 minutes pour être sûr que tout le créneau est
        // couvert
        // (Ou on peut vérifier juste Start, End et les changements de règles, mais 15m
        // est plus simple et robuste ici)
        for (LocalDateTime current = startDateTime; current.isBefore(endDateTime); current = current.plusMinutes(15)) {
            if (!isProviderAvailableAt(providerId, current)) {
                return false;
            }
        }
        // Vérifier aussi exactement la fin
        return isProviderAvailableAt(providerId, endDateTime.minusSeconds(1));
    }

    private boolean isProviderAvailableAt(Long providerId, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DayOfWeek dow = date.getDayOfWeek();

        // Récupérer toutes les règles (hebdomadaires et spécifiques)
        List<ProviderAvailability> weeklyRules = providerAvailabilityRepository
                .findByProvider_IdAndSpecificDateIsNull(providerId);
        List<ProviderAvailability> exceptions = providerAvailabilityRepository
                .findByProvider_IdAndSpecificDate(providerId, date);

        // 1. Est-ce un créneau de TRAVAIL ?
        boolean isWorking = false;

        // Vérifier d'abord les exceptions prioritaires (ponctuelles)
        if (!exceptions.isEmpty()) {
            isWorking = exceptions.stream()
                    .filter(ProviderAvailability::isAvailable)
                    .anyMatch(r -> !time.isBefore(r.getStartTime()) && time.isBefore(r.getEndTime()));

            // Si le jour est marqué comme bloqué (disponibilité=false sans heure précise ou
            // couvrant tout)
            // on vérifie s'il y a un blocage explicite sur ce créneau
            boolean isExplicitlyBlockedByException = exceptions.stream()
                    .filter(r -> !r.isAvailable())
                    .anyMatch(r -> !time.isBefore(r.getStartTime()) && time.isBefore(r.getEndTime()));

            if (isExplicitlyBlockedByException)
                return false;
        }

        // Si pas d'exception de travail, on regarde les règles hebdomadaires
        if (!isWorking) {
            isWorking = weeklyRules.stream()
                    .filter(r -> r.getDayOfWeek() == dow && r.isAvailable())
                    .anyMatch(r -> !time.isBefore(r.getStartTime()) && time.isBefore(r.getEndTime()));
        }

        if (!isWorking)
            return false;

        // 2. Est-ce un créneau d'INDISPONIBILITÉ (Exclusion) ?
        // Même si je suis censé travailler, j'ai pu marquer une pause
        boolean isUnavailability = weeklyRules.stream()
                .filter(r -> r.getDayOfWeek() == dow && !r.isAvailable())
                .anyMatch(r -> !time.isBefore(r.getStartTime()) && time.isBefore(r.getEndTime()));

        if (isUnavailability)
            return false;

        // 3. Conflits avec requêtes existantes (ACCEPTED ou IN_PROGRESS)
        return serviceRequestRepository.findByAssignedProvider_IdOrderByCreatedAtDesc(providerId).stream()
                .filter(req -> (req.getStatus() == ServiceRequestStatus.ACCEPTED
                        || req.getStatus() == ServiceRequestStatus.IN_PROGRESS)
                        && req.getRequestedDateTime().toLocalDate().equals(date))
                .noneMatch(req -> {
                    LocalTime reqStart = req.getRequestedDateTime().toLocalTime();
                    int duration = req.getService().getDurationMinutes() != null ? req.getService().getDurationMinutes()
                            : 60;
                    LocalTime reqEnd = reqStart.plusMinutes(duration);
                    return !time.isBefore(reqStart) && time.isBefore(reqEnd);
                });
    }

    @Override
    public ProviderProfileDTO getProviderProfile(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerId));
        ProviderProfileDTO dto = toProviderProfileDTO(provider);

        List<ServiceReview> reviews = serviceReviewRepository.findByProvider_IdOrderByCreatedAtDesc(providerId);
        dto.setReviews(reviews.stream().map(r -> ProviderProfileDTO.ReviewDTO.builder()
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt().toString())
                .patientName(r.getPatient() != null ? r.getPatient().getFullName() : "Anonyme")
                .build()).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<ServiceReview> getProviderReviews(Long providerId) {
        return serviceReviewRepository.findByProvider_IdOrderByCreatedAtDesc(providerId);
    }

    /**
     * Calcule les créneaux disponibles d'un provider sur une période donnée.
     * Algorithme :
     * Pour chaque jour dans [from, to] :
     * 1. Chercher une exception ponctuelle (specificDate == jour)
     * 2. Si exception avec available=false → jour bloqué ❌
     * 3. Sinon, chercher la règle hebdomadaire (dayOfWeek matching)
     * 4. Si règle avec available=true → créneau disponible ✅
     */
    @Override
    public List<AvailableSlotDTO> getProviderAvailableSlots(Long providerId, LocalDate from, LocalDate to) {
        List<AvailableSlotDTO> slots = new ArrayList<>();
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerId));

        List<ProviderAvailability> weeklyRules = providerAvailabilityRepository
                .findByProvider_IdAndSpecificDateIsNull(providerId);

        LocalDate current = from;
        while (!current.isAfter(to)) {
            final LocalDate day = current;

            // Vérifier exceptions du jour (jours bloqués ou spéciaux)
            List<ProviderAvailability> exceptions = providerAvailabilityRepository
                    .findByProvider_IdAndSpecificDate(providerId, day);

            boolean blockedByException = exceptions.stream().anyMatch(e -> !e.isAvailable());

            if (!blockedByException) {
                // 1. Chercher les requêtes déjà occupées
                List<ServiceRequest> busyRequests = serviceRequestRepository
                        .findByAssignedProvider_IdOrderByCreatedAtDesc(providerId)
                        .stream()
                        .filter(req -> (req.getStatus() == ServiceRequestStatus.ACCEPTED
                                || req.getStatus() == ServiceRequestStatus.IN_PROGRESS)
                                && req.getRequestedDateTime().toLocalDate().equals(day))
                        .collect(Collectors.toList());

                DayOfWeek dow = day.getDayOfWeek();

                // 2. Chercher les règles d'indisponibilité (Exclusions)
                List<ProviderAvailability> exclusions = weeklyRules.stream()
                        .filter(r -> r.getDayOfWeek() == dow && !r.isAvailable())
                        .collect(Collectors.toList());
                exclusions.addAll(exceptions.stream().filter(e -> !e.isAvailable()).collect(Collectors.toList()));

                // 3. Chercher les règles de travail (Inclusions)
                List<ProviderAvailability> workingRules = weeklyRules.stream()
                        .filter(r -> r.getDayOfWeek() == dow && r.isAvailable())
                        .collect(Collectors.toList());
                workingRules.addAll(exceptions.stream().filter(e -> e.isAvailable()).collect(Collectors.toList()));

                for (ProviderAvailability work : workingRules) {
                    List<AvailableSlotDTO> daySlots = new ArrayList<>();
                    daySlots.add(AvailableSlotDTO.builder()
                            .date(day).dayOfWeek(dow)
                            .startTime(work.getStartTime()).endTime(work.getEndTime())
                            .build());

                    // Soustraire les exclusions (pauses)
                    for (ProviderAvailability excl : exclusions) {
                        List<AvailableSlotDTO> nextLevel = new ArrayList<>();
                        for (AvailableSlotDTO currentSlot : daySlots) {
                            nextLevel.addAll(splitSlot(currentSlot, excl.getStartTime(), excl.getEndTime()));
                        }
                        daySlots = nextLevel;
                    }

                    // Soustraire les rendez-vous occupés
                    for (ServiceRequest req : busyRequests) {
                        LocalTime start = req.getRequestedDateTime().toLocalTime();
                        int duration = req.getService().getDurationMinutes() != null
                                ? req.getService().getDurationMinutes()
                                : 60;
                        LocalTime end = start.plusMinutes(duration);

                        List<AvailableSlotDTO> nextLevel = new ArrayList<>();
                        for (AvailableSlotDTO currentSlot : daySlots) {
                            nextLevel.addAll(splitSlot(currentSlot, start, end));
                        }
                        daySlots = nextLevel;
                    }

                    slots.addAll(daySlots);
                }
            }
            current = current.plusDays(1);
        }
        return slots;
    }

    /**
     * ✅ Get blocked/unavailable dates for a provider on a date range
     * Returns a list of YYYY-MM-DD date strings where provider is marked
     * unavailable (available=false)
     */
    @Override
    public List<String> getProviderBlockedDates(Long providerId, LocalDate from, LocalDate to) {
        List<String> blockedDates = new ArrayList<>();

        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerId));

        // Get specific date rules (both blocked and available exceptions)
        LocalDate current = from;
        while (!current.isAfter(to)) {
            // Check for specific date availability exceptions
            List<ProviderAvailability> specificRules = providerAvailabilityRepository
                    .findByProvider_IdAndSpecificDate(providerId, current);

            // If there's a specific rule for this date with available=false → blocked
            boolean isBlockedBySpecificRule = specificRules.stream()
                    .anyMatch(r -> !r.isAvailable());

            if (isBlockedBySpecificRule) {
                blockedDates.add(current.toString());
            }

            current = current.plusDays(1);
        }

        return blockedDates;
    }

    // ── Patient ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ServiceRequest createRequest(String patientEmail, CreateServiceRequestDTO dto) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Compte utilisateur introuvable"));

        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Votre profil Patient est incomplet ou introuvable. Veuillez recréer votre compte depuis la page Inscription."));

        HomeCareService service = homeCareServiceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found: " + dto.getServiceId()));

        // ✅ VALIDATION D'INTERVENTION DATE (doit être aujourd'hui ou futur, max 90
        // jours)
        com.aziz.demosec.util.InterventionDateValidator.validate(dto.getRequestedDateTime());

        int duration = service.getDurationMinutes() != null ? service.getDurationMinutes() : 60;

        // ✅ VALIDATION DE DISPONIBILITÉ DU PRESTATAIRE (vérifier jours bloqués)
        // Si le patient a choisi un prestataire spécifique
        if (dto.getProviderId() != null) {
            ServiceProvider chosen = serviceProviderRepository.findById(dto.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Provider not found: " + dto.getProviderId()));

            // Vérifier disponibilité du prestataire pour cette date
            if (!isProviderAvailableFor(chosen.getId(), dto.getRequestedDateTime(), duration)) {
                LocalDate requestedDate = dto.getRequestedDateTime().toLocalDate();
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Le prestataire sélectionné n'est pas disponible le " + requestedDate +
                                ". Ses jours travail ou sa disponibilité empêchent cette réservation.");
            }

            if (!chosen.isVerified()) {
                throw new RuntimeException("Le prestataire sélectionné n'est pas encore vérifié");
            }
        }

        ServiceRequest request = ServiceRequest.builder()
                .patient(patient)
                .service(service)
                .status(ServiceRequestStatus.PENDING)
                .requestedDateTime(dto.getRequestedDateTime())
                .address(dto.getAddress())
                .patientNotes(dto.getPatientNotes())
                .price(service.getPrice())
                .build();

        // Si le patient a choisi un prestataire spécifique
        if (dto.getProviderId() != null) {
            ServiceProvider chosen = serviceProviderRepository.findById(dto.getProviderId())
                    .orElseThrow(() -> new RuntimeException("Provider not found: " + dto.getProviderId()));

            request.setAssignedProvider(chosen);
            log.info("Patient chose provider {} directly", chosen.getId());
        } else {
            // Auto-assignation : premier provider vérifié disponible pour ce service
            List<ServiceProvider> providers = serviceProviderRepository.findVerifiedByServiceId(dto.getServiceId());
            ServiceProvider availableManual = providers.stream()
                    .filter(p -> isProviderAvailableFor(p.getId(), dto.getRequestedDateTime(), duration))
                    .findFirst()
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.BAD_REQUEST,
                            "Désolé, aucun prestataire n'est disponible à cette heure pour la durée requise."));

            request.setAssignedProvider(availableManual);
            log.info("Auto-assigned provider {} to request", availableManual.getId());
        }

        ServiceRequest saved = serviceRequestRepository.save(request);
        if (saved.getAssignedProvider() != null) {
            sendProviderNotification(saved, "✅ Une nouvelle demande d'intervention vous a été assignée !");
        }
        sendStatusNotification(saved);
        return saved;
    }

    @Override
    public List<ServiceRequest> getMyRequests(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientEmail));
        List<ServiceRequest> requests = serviceRequestRepository.findByPatientOrderByCreatedAtDesc(user);
        for (ServiceRequest req : requests) {
            req.setReviewed(serviceReviewRepository.findByRequest_Id(req.getId()).isPresent());
        }
        return requests;
    }

    @Override
    public ServiceRequest getRequestById(Long requestId) {
        return serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId, String patientEmail) {
        ServiceRequest request = getRequestById(requestId);
        if (!request.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("Unauthorized");
        }
        if (request.getStatus() == ServiceRequestStatus.IN_PROGRESS ||
                request.getStatus() == ServiceRequestStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a request in status: " + request.getStatus());
        }

        // Utilisation de la méthode JPQL (Optimisation)
        serviceRequestRepository.updateStatus(requestId, ServiceRequestStatus.CANCELLED);

        // Notifier le prestataire si la demande était ACCEPTED
        if (request.getAssignedProvider() != null) {
            sendProviderNotification(request, "❌ Une demande qui vous était assignée a été annulée par le patient.");
        }
    }

    @Override
    @Transactional
    public ServiceRequest completeRequestAsPatient(Long requestId, String patientEmail) {
        ServiceRequest request = getRequestById(requestId);
        if (!request.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("Unauthorized");
        }
        if (request.getStatus() != ServiceRequestStatus.IN_PROGRESS) {
            throw new RuntimeException("Seules les demandes IN_PROGRESS peuvent être terminées par le patient");
        }
        request.setStatus(ServiceRequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        ServiceRequest saved = serviceRequestRepository.save(request);
        if (saved.getAssignedProvider() != null) {
            sendProviderNotification(saved,
                    "✅ Le patient a confirmé la fin de l'intervention ! L'intervention est terminée.");
        }
        return saved;
    }

    @Override
    @Transactional
    public ServiceReview submitReview(Long requestId, String patientEmail, SubmitReviewDTO dto) {
        ServiceRequest request = getRequestById(requestId);

        if (request.getPatient() == null || !request.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("Unauthorized");
        }
        if (request.getStatus() != ServiceRequestStatus.COMPLETED) {
            throw new RuntimeException("Can only review a COMPLETED request");
        }
        if (request.getAssignedProvider() == null) {
            throw new RuntimeException("Cannot review a request with no assigned provider");
        }
        if (serviceReviewRepository.findByRequest_Id(requestId).isPresent()) {
            throw new RuntimeException("Review already submitted for this request");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        ServiceReview review = ServiceReview.builder()
                .request(request)
                .patient(request.getPatient())
                .provider(request.getAssignedProvider())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        ServiceReview saved = serviceReviewRepository.save(review);
        updateProviderRating(request.getAssignedProvider().getId());
        return saved;
    }

    // ── Prestataire ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getProviderRequests(String providerEmail) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Provider User not found"));
        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElse(null);

        if (provider == null) {
            log.warn("User {} has role HOME_CARE_PROVIDER but no ServiceProvider profile exists. Returning empty list.",
                    providerEmail);
            return List.of();
        }

        List<ServiceRequest> requests = serviceRequestRepository
                .findByAssignedProvider_IdOrderByCreatedAtDesc(provider.getId());
        for (ServiceRequest req : requests) {
            req.setReviewed(serviceReviewRepository.findByRequest_Id(req.getId()).isPresent());
        }
        return requests;
    }

    @Override
    @Transactional
    public ServiceRequest acceptRequest(Long requestId, String providerEmail) {
        ServiceRequest request = getRequestById(requestId);
        verifyProviderOwnership(request, providerEmail);

        if (request.getStatus() != ServiceRequestStatus.PENDING) {
            throw new RuntimeException("Request must be PENDING to accept");
        }

        request.setStatus(ServiceRequestStatus.ACCEPTED);
        request.setAssignedDateTime(LocalDateTime.now());
        ServiceRequest saved = serviceRequestRepository.save(request);
        sendStatusNotification(saved);
        return saved;
    }

    @Override
    @Transactional
    public ServiceRequest declineRequest(Long requestId, String providerEmail) {
        ServiceRequest request = getRequestById(requestId);
        verifyProviderOwnership(request, providerEmail);

        if (request.getStatus() != ServiceRequestStatus.PENDING) {
            throw new RuntimeException("Request must be PENDING to decline");
        }

        request.setStatus(ServiceRequestStatus.CANCELLED);
        request.setProviderNotes("Declined by provider");
        ServiceRequest saved = serviceRequestRepository.save(request);
        sendStatusNotification(saved); // Notifier le patient que la demande est annulée
        return saved;
    }

    @Override
    @Transactional
    public ServiceRequest startRequest(Long requestId, String providerEmail) {
        ServiceRequest request = getRequestById(requestId);
        verifyProviderOwnership(request, providerEmail);
        if (request.getStatus() != ServiceRequestStatus.ACCEPTED) {
            throw new RuntimeException("Request must be ACCEPTED to start");
        }
        request.setStatus(ServiceRequestStatus.IN_PROGRESS);
        ServiceRequest saved = serviceRequestRepository.save(request);
        sendStatusNotification(saved);
        return saved;
    }

    @Override
    @Transactional
    public ServiceRequest completeRequest(Long requestId, String providerEmail, String providerNotes) {
        ServiceRequest request = getRequestById(requestId);
        verifyProviderOwnership(request, providerEmail);
        if (request.getStatus() != ServiceRequestStatus.IN_PROGRESS) {
            throw new RuntimeException("Request must be IN_PROGRESS to complete");
        }
        request.setStatus(ServiceRequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        request.setProviderNotes(providerNotes);
        ServiceRequest saved = serviceRequestRepository.save(request);
        sendStatusNotification(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderAvailability> getMyAvailability(String providerEmail) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElse(null);

        if (provider == null) {
            log.warn("User {} has role HOME_CARE_PROVIDER but no ServiceProvider profile. Returning empty list.",
                    providerEmail);
            return new java.util.ArrayList<>();
        }

        return providerAvailabilityRepository.findByProvider_Id(provider.getId());
    }

    @Override
    @Transactional
    public ProviderAvailability saveAvailability(String providerEmail, AvailabilityDTO dto) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("No ServiceProvider profile"));

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new RuntimeException("L'heure de fin doit être après l'heure de début.");
        }

        ProviderAvailability availability = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .available(dto.isAvailable())
                .specificDate(dto.getSpecificDate())
                .build();

        return providerAvailabilityRepository.save(availability);
    }

    @Override
    @Transactional
    public void deleteAvailability(Long availabilityId, String providerEmail) {
        ProviderAvailability av = providerAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        // Vérification d'ownership
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("No ServiceProvider profile"));
        if (!av.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized: cette disponibilité ne vous appartient pas");
        }
        providerAvailabilityRepository.delete(av);
    }

    /**
     * Bloque un jour entier en créant un enregistrement d'exception avec
     * available=false.
     * Cela surpasse toute règle hebdomadaire pour cette date.
     */
    @Override
    @Transactional
    public ProviderAvailability blockDay(String providerEmail, LocalDate date) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("No ServiceProvider profile"));

        // Supprimer tout blocage existant pour ce jour avant d'en créer un nouveau
        List<ProviderAvailability> existing = providerAvailabilityRepository
                .findByProvider_IdAndSpecificDate(provider.getId(), date);
        providerAvailabilityRepository.deleteAll(existing);

        ProviderAvailability blockedDay = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(date.getDayOfWeek())
                .specificDate(date)
                .startTime(java.time.LocalTime.of(0, 0))
                .endTime(java.time.LocalTime.of(23, 59))
                .available(false)
                .build();

        return providerAvailabilityRepository.save(blockedDay);
    }

    // ── Admin ──────────────────────────────────────────────────────────────

    @Override
    public List<ServiceProvider> getAllProviders() {
        return serviceProviderRepository.findAll();
    }

    @Override
    public List<ServiceProvider> getPendingProviders() {
        return serviceProviderRepository.findByVerifiedFalse();
    }

    @Override
    @Transactional
    public ServiceProvider verifyProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerId));
        provider.setVerified(true);
        if (provider.getUser() != null) {
            provider.getUser().setEnabled(true);
            userRepository.save(provider.getUser());
        }
        return serviceProviderRepository.save(provider);
    }

    @Override
    @Transactional
    public void rejectProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerId));
        User user = provider.getUser();
        serviceProviderRepository.delete(provider);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public HomeCareService createService(HomeCareService service) {
        return homeCareServiceRepository.save(service);
    }

    @Override
    @Transactional
    public HomeCareService updateService(Long id, HomeCareService updated) {
        HomeCareService existing = homeCareServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setActive(updated.isActive());
        existing.setDurationMinutes(updated.getDurationMinutes());
        return homeCareServiceRepository.save(existing);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void verifyProviderOwnership(ServiceRequest request, String providerEmail) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("No ServiceProvider profile"));
        if (request.getAssignedProvider() == null ||
                !request.getAssignedProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized: cette demande ne vous est pas assignée");
        }
    }

    private ProviderProfileDTO toProviderProfileDTO(ServiceProvider provider) {
        List<ProviderProfileDTO.ServiceSummaryDTO> specialties = provider.getSpecialties().stream()
                .map(s -> ProviderProfileDTO.ServiceSummaryDTO.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .iconUrl(s.getIconUrl())
                        .build())
                .collect(Collectors.toList());

        return ProviderProfileDTO.builder()
                .id(provider.getId())
                .fullName(provider.getUser() != null ? provider.getUser().getFullName() : "—")
                .bio(provider.getBio())
                .profilePictureUrl(provider.getProfilePictureUrl())
                .averageRating(provider.getAverageRating())
                .totalReviews(provider.getTotalReviews())
                .specialties(specialties)
                .reviews(new java.util.ArrayList<>())
                .build();
    }

    private void sendStatusNotification(ServiceRequest request) {
        if (request.getPatient() == null)
            return;
        String patientEmail = request.getPatient().getEmail();
        String message = switch (request.getStatus()) {
            case ACCEPTED -> "✅ Votre demande a été acceptée. Un prestataire vous a été assigné.";
            case IN_PROGRESS -> "🚗 Votre prestataire est en route !";
            case COMPLETED -> "🎉 Service terminé ! N'oubliez pas de laisser un avis.";
            case CANCELLED -> "❌ Votre demande a été annulée.";
            default -> "📋 Statut de votre demande mis à jour : " + request.getStatus();
        };
        try {
            messagingTemplate.convertAndSendToUser(patientEmail, "/queue/homecare", message);
        } catch (Exception e) {
            log.warn("Could not send WebSocket notification to {}: {}", patientEmail, e.getMessage());
        }
    }

    private void sendProviderNotification(ServiceRequest request, String message) {
        if (request.getAssignedProvider() == null || request.getAssignedProvider().getUser() == null)
            return;

        User providerUser = request.getAssignedProvider().getUser();
        String providerEmail = providerUser.getEmail();

        try {
            // Create a notification record in the database
            Notification notification = Notification.builder()
                    .recipient(providerUser)
                    .title("Nouvelle demande d'intervention")
                    .message(message)
                    .type(NotificationType.NEW_HOMECARE_REQUEST)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            Notification saved = notificationRepository.save(notification);

            // Convert to DTO for WebSocket transmission
            NotificationResponseDTO dto = NotificationResponseDTO.builder()
                    .id(saved.getId())
                    .recipientId(providerUser.getId())
                    .title(saved.getTitle())
                    .message(saved.getMessage())
                    .type(saved.getType())
                    .isRead(saved.isRead())
                    .createdAt(saved.getCreatedAt())
                    .build();

            // Send real-time WebSocket notification to provider
            messagingTemplate.convertAndSendToUser(providerEmail, "/queue/notifications", dto);
            log.info("Notification sent to provider {} for request {}", providerEmail, request.getId());
        } catch (Exception e) {
            log.warn("Could not send WebSocket notification to provider {}: {}", providerEmail, e.getMessage());
        }
    }

    private void updateProviderRating(Long providerId) {
        List<ServiceReview> reviews = serviceReviewRepository.findByProvider_Id(providerId);
        if (reviews.isEmpty())
            return;
        double avg = reviews.stream().mapToInt(ServiceReview::getRating).average().orElse(0);
        serviceProviderRepository.findById(providerId).ifPresent(provider -> {
            provider.setAverageRating(avg);
            provider.setTotalReviews(reviews.size());
            serviceProviderRepository.save(provider);
        });
    }

    @Override
    @Transactional
    public void addSpecialtyToProvider(Long providerId, Long serviceId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        HomeCareService service = homeCareServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Affectation Complexe (ManyToMany) : Ajout à la collection
        provider.getSpecialties().add(service);
        serviceProviderRepository.save(provider);
    }

    @Override
    @Transactional
    public void removeSpecialtyFromProvider(Long providerId, Long serviceId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        HomeCareService service = homeCareServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Affectation Complexe (ManyToMany) : Retrait de la collection
        provider.getSpecialties().remove(service);
        serviceProviderRepository.save(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getProviderCalendarEvents(String providerEmail, LocalDate from, LocalDate to) {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        ServiceProvider provider = serviceProviderRepository.findByUser_Id(user.getId())
                .orElse(null);

        if (provider == null) {
            log.warn("User {} has role HOME_CARE_PROVIDER but no ServiceProvider profile. Returning empty list.",
                    providerEmail);
            return new java.util.ArrayList<>();
        }

        Long providerId = provider.getId();

        List<CalendarEventDTO> events = new ArrayList<>();

        // 1. Ajouter les disponibilités (Règles hebdomadaires sur la période)
        List<ProviderAvailability> weeklyRules = providerAvailabilityRepository
                .findByProvider_IdAndSpecificDateIsNull(providerId);
        LocalDate current = from;
        while (!current.isAfter(to)) {
            final LocalDate day = current;
            DayOfWeek dow = day.getDayOfWeek();

            // On cherche le record spécifique de blocage pour récupérer son ID
            List<ProviderAvailability> exceptions = providerAvailabilityRepository
                    .findByProvider_IdAndSpecificDate(providerId, day);
            ProviderAvailability blockRecord = exceptions.stream()
                    .filter(e -> !e.isAvailable())
                    .findFirst().orElse(null);

            if (blockRecord != null) {
                events.add(CalendarEventDTO.builder()
                        .id(blockRecord.getId()) // ID du record pour pouvoir débloquer !
                        .start(day.atStartOfDay())
                        .end(day.atTime(23, 59))
                        .title("Bloqué (Indisponible)")
                        .type("BLOCKED")
                        .build());
            } else {
                // Règle hebdomadaire normale
                weeklyRules.stream()
                        .filter(r -> r.getDayOfWeek() == dow && r.isAvailable())
                        .forEach(r -> events.add(CalendarEventDTO.builder()
                                .id(r.getId())
                                .start(day.atTime(r.getStartTime()))
                                .end(day.atTime(r.getEndTime()))
                                .title("Disponible")
                                .type("AVAILABLE")
                                .build()));
            }
            current = current.plusDays(1);
        }

        // 2. Ajouter les requêtes (ACCEPTED, IN_PROGRESS, PENDING)
        serviceRequestRepository.findByAssignedProvider_IdOrderByCreatedAtDesc(providerId).stream()
                .filter(req -> !req.getRequestedDateTime().toLocalDate().isBefore(from)
                        && !req.getRequestedDateTime().toLocalDate().isAfter(to))
                .forEach(req -> {
                    int duration = req.getService().getDurationMinutes() != null ? req.getService().getDurationMinutes()
                            : 60;
                    events.add(CalendarEventDTO.builder()
                            .id(req.getId())
                            .requestId(req.getId())
                            .start(req.getRequestedDateTime())
                            .end(req.getRequestedDateTime().plusMinutes(duration))
                            .title(req.getService().getName() + " - "
                                    + (req.getPatient() != null ? req.getPatient().getFullName() : "Patient"))
                            .type("REQUEST")
                            .status(req.getStatus().name())
                            .patientName(req.getPatient() != null ? req.getPatient().getFullName() : "Patient")
                            .build());
                });

        return events;
    }

    private List<AvailableSlotDTO> splitSlot(AvailableSlotDTO slot, LocalTime blockStart, LocalTime blockEnd) {
        List<AvailableSlotDTO> result = new ArrayList<>();
        LocalTime slotStart = slot.getStartTime();
        LocalTime slotEnd = slot.getEndTime();

        // No overlap
        if (!slotStart.isBefore(blockEnd) || !slotEnd.isAfter(blockStart)) {
            result.add(slot);
            return result;
        }

        // Potential sub-slot 1 (before block)
        if (slotStart.isBefore(blockStart)) {
            result.add(AvailableSlotDTO.builder()
                    .date(slot.getDate()).dayOfWeek(slot.getDayOfWeek())
                    .startTime(slotStart).endTime(blockStart)
                    .build());
        }

        // Potential sub-slot 2 (after block)
        if (slotEnd.isAfter(blockEnd)) {
            result.add(AvailableSlotDTO.builder()
                    .date(slot.getDate()).dayOfWeek(slot.getDayOfWeek())
                    .startTime(blockEnd).endTime(slotEnd)
                    .build());
        }

        return result;
    }
}
