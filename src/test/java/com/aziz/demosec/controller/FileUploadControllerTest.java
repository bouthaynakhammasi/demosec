package com.aziz.demosec.controller;

import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void uploadFile_ShouldReturnUrlOnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "prescription.pdf", "application/pdf", "dummy-content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/uploads/prescriptions/")));
    }

    @Test
    void uploadFile_ShouldReturnBadRequest_WhenFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "", "application/pdf", new byte[0]
        );

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please select a file to upload"));
    }
}
