package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.Clinic;
import com.aziz.demosec.dto.ClinicProfileResponse;
import com.aziz.demosec.dto.ClinicProfileUpdateRequest;
import com.aziz.demosec.repository.ClinicRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClinicController.class)
@AutoConfigureMockMvc
class ClinicControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClinicRepository clinicRepository;

    private Clinic mockClinic;

    @BeforeEach
    void setUp() {
        mockClinic = new Clinic();
        mockClinic.setId(1L);
        mockClinic.setEmail("clinic@test.com");
        mockClinic.setPhone("12345678");
        mockClinic.setBirthDate(LocalDate.of(1990, 1, 1));
        mockClinic.setName("Test Clinic Name");
        mockClinic.setAddress("Test Address");
        // setName updates fullName, so we set fullName after if we want a different value
        // but it's more realistic to keep them same if the entity logic says so.
        // Let's just set it again for testing.
        mockClinic.setFullName("Test Clinic");
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void getMe_ShouldReturnProfile() throws Exception {
        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.of(mockClinic));

        mockMvc.perform(get("/api/clinics/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test Clinic"))
                .andExpect(jsonPath("$.email").value("clinic@test.com"))
                .andExpect(jsonPath("$.phone").value("12345678"))
                .andExpect(jsonPath("$.clinicName").value("Test Clinic Name"))
                .andExpect(jsonPath("$.address").value("Test Address"));
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void getMe_WhenNotFound_ShouldReturn404() throws Exception {
        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinics/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void updateProfile_ShouldUpdateAndReturnProfile() throws Exception {
        ClinicProfileUpdateRequest request = new ClinicProfileUpdateRequest(
                "Updated Name",
                "87654321",
                LocalDate.of(1995, 5, 5),
                "photo-url",
                "New Clinic Name",
                "New Address",
                45.0,
                5.0,
                true,
                true,
                "999",
                "888"
        );

        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.of(mockClinic));
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/clinics/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Clinic Name"))
                .andExpect(jsonPath("$.phone").value("87654321"))
                .andExpect(jsonPath("$.birthDate").value("1995-05-05"))
                .andExpect(jsonPath("$.photo").value("photo-url"))
                .andExpect(jsonPath("$.clinicName").value("New Clinic Name"))
                .andExpect(jsonPath("$.address").value("New Address"))
                .andExpect(jsonPath("$.latitude").value(45.0))
                .andExpect(jsonPath("$.longitude").value(5.0))
                .andExpect(jsonPath("$.hasEmergency").value(true))
                .andExpect(jsonPath("$.hasAmbulance").value(true))
                .andExpect(jsonPath("$.emergencyPhone").value("999"))
                .andExpect(jsonPath("$.ambulancePhone").value("888"));
    }

    @Test
    @WithMockUser(username = "clinic@test.com")
    void updateProfile_WhenNotFound_ShouldReturn404() throws Exception {
        ClinicProfileUpdateRequest request = new ClinicProfileUpdateRequest(
                "Updated Name", "87654321", LocalDate.of(1995, 5, 5), "photo-url",
                "New Clinic Name", "New Address", 45.0, 5.0, true, true, "999", "888"
        );

        when(clinicRepository.findByEmail("clinic@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clinics/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
