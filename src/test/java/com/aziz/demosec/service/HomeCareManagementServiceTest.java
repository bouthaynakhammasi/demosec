package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.homecare.CreateServiceRequestDTO;
import com.aziz.demosec.dto.homecare.ProviderProfileDTO;
import com.aziz.demosec.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeCareManagementServiceTest {

    @Mock private HomeCareServiceRepository homeCareServiceRepository;
    @Mock private ServiceProviderRepository serviceProviderRepository;
    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private ProviderAvailabilityRepository providerAvailabilityRepository;
    @Mock private ServiceReviewRepository serviceReviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private HomeCareManagementServiceImpl homeCareService;

    private HomeCareService medicalService;
    private ServiceProvider provider;

    @BeforeEach
    void setUp() {
        medicalService = new HomeCareService();
        medicalService.setId(1L);
        medicalService.setName("Nursing Care");
        medicalService.setActive(true);
        medicalService.setPrice(java.math.BigDecimal.valueOf(50.0));

        provider = new ServiceProvider();
        provider.setId(10L);
        provider.setVerified(true);
        provider.setAverageRating(4.5);
        provider.setSpecialties(new java.util.HashSet<>());
    }

    @Test
    void getAllActiveServices_ShouldReturnOnlyActive() {
        // Arrange
        when(homeCareServiceRepository.findByActiveTrue()).thenReturn(List.of(medicalService));

        // Act
        List<HomeCareService> result = homeCareService.getAllActiveServices();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void searchProviders_ShouldFilterByRating() {
        // Arrange
        when(serviceProviderRepository.findVerifiedByServiceId(1L)).thenReturn(List.of(provider));

        // Act
        List<ProviderProfileDTO> result = homeCareService.searchProviders(1L, 4.0);

        // Assert
        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0).getAverageRating());
    }

    @Test
    void acceptRequest_ShouldChangeStatusToAccepted() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        request.setId(100L);
        request.setStatus(ServiceRequestStatus.PENDING);
        request.setAssignedProvider(provider);

        com.aziz.demosec.domain.User providerUser = new com.aziz.demosec.domain.User();
        providerUser.setId(50L);
        providerUser.setEmail("provider@test.com");
        provider.setUser(providerUser);

        when(serviceRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(userRepository.findByEmail("provider@test.com")).thenReturn(Optional.of(providerUser));
        when(serviceProviderRepository.findByUser_Id(50L)).thenReturn(Optional.of(provider));
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ServiceRequest result = homeCareService.acceptRequest(100L, "provider@test.com");

        // Assert
        assertEquals(ServiceRequestStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getAssignedDateTime());
    }

    @Test
    void verifyProvider_ShouldSetVerifiedTrue() {
        // Arrange
        provider.setVerified(false);
        when(serviceProviderRepository.findById(10L)).thenReturn(Optional.of(provider));
        when(serviceProviderRepository.save(any(ServiceProvider.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ServiceProvider result = homeCareService.verifyProvider(10L);

        // Assert
        assertTrue(result.isVerified());
    }
}
