package com.aziz.demosec.service;

import com.aziz.demosec.Entities.AidRequestStatus;
import com.aziz.demosec.Entities.DonationStatus;
import com.aziz.demosec.dto.donation.*;
import java.util.List;

public interface IDonationService {

    // ─── DONATION ─────────────────────────────────────────────────
    DonationResponseDTO createDonation(DonationRequestDTO dto);
    DonationResponseDTO getDonationById(Long id);
    List<DonationResponseDTO> getAllDonations();
    List<DonationResponseDTO> getDonationsByStatus(DonationStatus status);
    DonationResponseDTO updateDonation(Long id, DonationRequestDTO dto);
    void deleteDonation(Long id);

    // ─── AID REQUEST ──────────────────────────────────────────────
    AidRequestResponseDTO createAidRequest(AidRequestDTO dto);
    AidRequestResponseDTO getAidRequestById(Long id);
    List<AidRequestResponseDTO> getAllAidRequests();
    List<AidRequestResponseDTO> getAidRequestsByPatient(Long patientId);
    AidRequestResponseDTO updateAidRequestStatus(Long id, AidRequestStatus status);
    void deleteAidRequest(Long id);

    // ─── ASSIGNMENT ───────────────────────────────────────────────
    DonationAssignmentResponseDTO assignDonation(DonationAssignmentDTO dto);
    List<DonationAssignmentResponseDTO> getAllAssignments();
}