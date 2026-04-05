package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.Entities.Doctor;
import com.aziz.demosec.Entities.Review;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.DoctorProfileDTO;
import com.aziz.demosec.dto.ReviewRequest;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.security.jwt.JwtService;
import com.aziz.demosec.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc // Enable filters to allow Authentication injection
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private DoctorRepository doctorRepository;
    @MockitoBean private ClinicRepository clinicRepository;
    @MockitoBean private ReviewRepository reviewRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private AppointmentRepository appointmentRepository;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

    private Doctor doctor;
    private DoctorProfileDTO doctorProfileDTO;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFullName("Dr. Gregory House");
        doctor.setEmail("house@ppth.com");
        doctor.setSpecialty("Diagnostic Medicine");
        doctor.setEnabled(true);

        doctorProfileDTO = DoctorProfileDTO.builder()
                .id(1L)
                .fullName("Dr. Gregory House")
                .specialty("Diagnostic Medicine")
                .consultationFee(new BigDecimal("200.00"))
                .build();
    }

    @Test
    @WithMockUser
    void getProfile_Success() throws Exception {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());
        when(reviewRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/doctors/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. Gregory House"));
    }

    @Test
    @WithMockUser
    void updateProfile_Success() throws Exception {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());
        when(reviewRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(put("/api/v1/doctors/1/profile")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorProfileDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAllDoctors_Success() throws Exception {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());
        when(reviewRepository.findByDoctorId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addReview_Success() throws Exception {
        User patient = new User();
        patient.setId(10L);
        patient.setFullName("Test Patient");
        patient.setEmail("patient@test.com");

        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent!");
        reviewRequest.setIsAnonymous(false);

        Review savedReview = Review.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .rating(5)
                .comment("Excellent!")
                .build();

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(reviewRepository.save(any())).thenReturn(savedReview);
        when(reviewRepository.findByDoctorId(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/api/v1/doctors/1/reviews")
                .with(user("patient@test.com"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Excellent!"));
    }
}
