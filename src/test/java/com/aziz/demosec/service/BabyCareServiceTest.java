package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.Mapper.BabyCareMapper;
import com.aziz.demosec.dto.baby.*;
import com.aziz.demosec.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BabyCareServiceTest {

    @Mock
    private BabyProfileRepository babyRepository;
    @Mock
    private VaccinationRepository vaccineRepository;
    @Mock
    private JournalEntryRepository journalRepository;
    @Mock
    private ParentPreferenceRepository preferenceRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DiaperRecordRepository diaperRepository;
    @Mock
    private BabyCareMapper mapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BabyCareService babyCareService;

    private Patient parent;
    private BabyProfile babyProfile;
    private BabyProfileRequestDTO profileRequest;

    @BeforeEach
    void setUp() {
        parent = new Patient();
        parent.setId(1L);

        babyProfile = BabyProfile.builder()
                .id(10L)
                .name("Alex")
                .birthDate(LocalDate.now().minusMonths(3))
                .gender(Gender.MALE)
                .parent(parent)
                .build();

        profileRequest = new BabyProfileRequestDTO();
        profileRequest.setName("Alex");
        profileRequest.setBirthDate(LocalDate.now().minusMonths(3));
        profileRequest.setGender("MALE");
        profileRequest.setBirthWeight(3.5);
        profileRequest.setBirthHeight(50.0);
    }

    @Test
    @DisplayName("Should successfully create a baby profile")
    void createProfile_Success() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(babyRepository.save(any(BabyProfile.class))).thenReturn(babyProfile);
        when(mapper.toResponseDTO(any(BabyProfile.class))).thenReturn(new BabyProfileResponseDTO());

        // Act
        BabyProfileResponseDTO result = babyCareService.createProfile(1L, profileRequest);

        // Assert
        assertNotNull(result);
        verify(patientRepository).findById(1L);
        verify(babyRepository).save(any(BabyProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when parent not found")
    void createProfile_ParentNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> babyCareService.createProfile(1L, profileRequest));
        verify(babyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully add a journal entry")
    void addJournalEntry_Success() {
        // Arrange
        when(babyRepository.findById(10L)).thenReturn(Optional.of(babyProfile));
        JournalEntry entry = JournalEntry.builder().id(100L).build();
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(entry);
        when(mapper.toJournalResponseDTO(any(JournalEntry.class))).thenReturn(new JournalEntryResponseDTO());

        // Act
        JournalEntryResponseDTO result = babyCareService.addJournalEntry(10L, JournalEntryType.FEEDING, "Milk 120ml", "Good appetite", null);

        // Assert
        assertNotNull(result);
        verify(journalRepository).save(any(JournalEntry.class));
    }

    @Test
    @DisplayName("Should validate sleep duration in journal entry")
    void addJournalEntry_InvalidSleepDuration() throws Exception {
        // Arrange
        when(babyRepository.findById(10L)).thenReturn(Optional.of(babyProfile));
        
        // Mocking ObjectMapper to return high duration
        com.fasterxml.jackson.databind.JsonNode node = mock(com.fasterxml.jackson.databind.JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(node);
        when(node.has("totalDurationSeconds")).thenReturn(true);
        when(node.get("totalDurationSeconds")).thenReturn(mock(com.fasterxml.jackson.databind.JsonNode.class));
        when(node.get("totalDurationSeconds").asLong()).thenReturn(100000L); // Over 24h (86400s)

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            babyCareService.addJournalEntry(10L, JournalEntryType.SLEEP, "100000s", null, "{\"totalDurationSeconds\":100000}")
        );
        assertTrue(exception.getMessage().contains("24 heures"));
    }

    @Test
    @DisplayName("Should fetch dashboard with correct data mapping")
    void getDashboard_Success() {
        // Arrange
        when(babyRepository.findById(10L)).thenReturn(Optional.of(babyProfile));
        when(vaccineRepository.findByBabyId(10L)).thenReturn(Collections.emptyList());
        when(journalRepository.findByBabyProfileIdOrderByCreatedAtDesc(10L)).thenReturn(Collections.emptyList());
        when(diaperRepository.findByBabyProfileIdOrderByChangedAtDesc(10L)).thenReturn(Collections.emptyList());

        // Act
        BabyDashboardDTO dashboard = babyCareService.getDashboard(10L);

        // Assert
        assertNotNull(dashboard);
        assertEquals("Alex", dashboard.getName());
        assertEquals("3 months", dashboard.getAge());
        verify(babyRepository).findById(10L);
    }

    @Test
    @DisplayName("Should successfully delete vaccine record")
    void deleteVaccineRecord_Success() {
        // Arrange
        when(vaccineRepository.findByBabyIdAndVaccineName(10L, "BCG"))
                .thenReturn(Collections.singletonList(new Vaccination()));

        // Act
        babyCareService.deleteVaccineRecord(10L, "BCG");

        // Assert
        verify(vaccineRepository).deleteAll(anyList());
    }
}
