package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.*;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientRepository patientRepository;
    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;
    @MockitoBean
    private ConsultationRepository consultationRepository;
    @MockitoBean
    private TreatmentRepository treatmentRepository;
    @MockitoBean
    private PrescriptionRepository prescriptionRepository;
    @MockitoBean
    private DiagnosisRepository diagnosisRepository;
    @MockitoBean
    private AppointmentRepository appointmentRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private DoctorRepository doctorRepository;
    @MockitoBean
    private LifestyleGoalRepository lifestyleGoalRepository;
    @MockitoBean
    private ProgressTrackingRepository progressTrackingRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.aziz.demosec.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFullName("Patient Zero");
        patient.setEmail("patient@example.com");
    }

    @Test
    @WithMockUser
    void getPatientById_ShouldReturnPatient() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Patient Zero"));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnListOfPatients() throws Exception {
        when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "patient@example.com")
    void getMyProfile_ShouldReturnCurrentPatient() throws Exception {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/api/patients/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("patient@example.com"));
    }

    @Test
    @WithMockUser(username = "patient@example.com")
    void updateProfile_ShouldReturnUpdatedPatient() throws Exception {
        PatientProfileUpdateRequest request = new PatientProfileUpdateRequest(
                "Updated Name", "patient@example.com", "12345678", 
                java.time.LocalDate.parse("1990-01-01"), "MALE",
                "Emergency", "111222333", 180.0, 75.0, 
                "B_POS", "None", "None", "photo.jpg"
        );

        when(patientRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/patients/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }
}
