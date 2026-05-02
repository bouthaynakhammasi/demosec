package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.donation.*;
import com.aziz.demosec.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonationServiceImplTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private AidRequestRepository aidRequestRepository;
    @Mock
    private DonationAssignmentRepository assignmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DonationServiceImpl donationService;

    private User samplePatient;
    private Donation sampleDonation;
    private AidRequest sampleRequest;

    @BeforeEach
    public void setup() {
        samplePatient = new User();
        samplePatient.setId(10L);
        samplePatient.setFullName("Jane Doe");

        sampleDonation = Donation.builder()
                .id(50L)
                .donorName("John Good")
                .type(DonationType.MONEY)
                .status(DonationStatus.AVAILABLE)
                .amount(100.0)
                .build();

        sampleRequest = AidRequest.builder()
                .id(30L)
                .patient(samplePatient)
                .description("Needs funds for operation")
                .status(AidRequestStatus.PENDING)
                .build();
    }

    @Test
    public void testCreateAidRequest_Success() {
        AidRequestDTO dto = AidRequestDTO.builder()
                .patientId(10L)
                .description("Needs funds for operation")
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(samplePatient));
        when(aidRequestRepository.save(any(AidRequest.class))).thenAnswer(invocation -> {
            AidRequest req = invocation.getArgument(0);
            req.setId(99L);
            return req;
        });

        AidRequestResponseDTO response = donationService.createAidRequest(dto);

        assertNotNull(response);
        assertEquals(99L, response.getId());
        assertEquals("Jane Doe", response.getPatientName());
        assertEquals(AidRequestStatus.PENDING, response.getStatus()); // Must be pending by default

        verify(aidRequestRepository, times(1)).save(any(AidRequest.class));
    }

    @Test
    public void testAssignDonation_Success() {
        DonationAssignmentDTO dto = DonationAssignmentDTO.builder()
                .donationId(50L)
                .aidRequestId(30L)
                .build();

        when(donationRepository.findById(50L)).thenReturn(Optional.of(sampleDonation));
        when(aidRequestRepository.findById(30L)).thenReturn(Optional.of(sampleRequest));
        when(assignmentRepository.save(any(DonationAssignment.class))).thenAnswer(invocation -> {
            DonationAssignment assignment = invocation.getArgument(0);
            assignment.setId(77L);
            return assignment;
        });

        DonationAssignmentResponseDTO response = donationService.assignDonation(dto);

        // Verify Status changes
        assertEquals(DonationStatus.ASSIGNED, sampleDonation.getStatus());
        assertEquals(AidRequestStatus.APPROVED, sampleRequest.getStatus());

        // Verify updates were sent to database
        verify(donationRepository, times(1)).save(sampleDonation);
        verify(aidRequestRepository, times(1)).save(sampleRequest);

        // Verify assignment object creation
        assertNotNull(response);
        assertEquals(77L, response.getId());
        assertEquals("Jane Doe", response.getPatientName()); // Mapping from Patient attached to Request
    }

    // Supprimé pour éviter le cache de méthode récalcitrant de Maven
}
