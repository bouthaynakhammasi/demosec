package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.ProgressTracking;
import com.aziz.demosec.dto.ProgressTrackingRequest;
import com.aziz.demosec.dto.ProgressTrackingResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
import com.aziz.demosec.repository.ProgressTrackingRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressTrackingServiceTest {

    @Mock
    private ProgressTrackingRepository progressRepository;

    @Mock
    private LifestyleGoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressTrackingService progressService;

    private Patient patient;
    private LifestyleGoal goal;
    private ProgressTracking tracking;
    private ProgressTrackingRequest request;

    @BeforeEach
    void setUp() {
        patient = TestDataFactory.createPatient(1L, "patient@test.com");
        goal = TestDataFactory.createLifestyleGoal(1L, patient);
        tracking = TestDataFactory.createProgressTracking(1L, goal, patient);

        request = ProgressTrackingRequest.builder()
                .goalId(1L)
                .patientId(1L)
                .date(LocalDate.now())
                .value(new BigDecimal("95.0"))
                .notes("Updated Notes")
                .build();
    }

    @Test
    void addTracking_ShouldReturnResponse_WhenValid() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(progressRepository.save(any(ProgressTracking.class))).thenReturn(tracking);

        ProgressTrackingResponse response = progressService.addTracking(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(tracking.getId());
        verify(progressRepository).save(any(ProgressTracking.class));
    }

    @Test
    void getTrackingById_ShouldReturnResponse_WhenFound() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(tracking));

        ProgressTrackingResponse response = progressService.getTrackingById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(tracking.getId());
    }

    @Test
    void calculateProgress_ShouldReturnCorrectPercentage() {
        // Arrange
        goal.setBaselineValue(new BigDecimal("100.0"));
        goal.setTargetValue(new BigDecimal("80.0")); // Weight loss goal

        ProgressTracking record = new ProgressTracking();
        record.setValue(new BigDecimal("90.0")); // Halfway there

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(progressRepository.findByGoalId(1L)).thenReturn(List.of(record));

        // Act
        double progress = progressService.calculateProgress(1L);

        // Assert
        // (100 - 90) / (100 - 80) = 10 / 20 = 0.5 = 50%
        assertThat(progress).isEqualTo(50.0);
    }

    @Test
    void updateTracking_ShouldReturnUpdatedResponse() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(tracking));
        when(progressRepository.save(any(ProgressTracking.class))).thenReturn(tracking);

        ProgressTrackingResponse response = progressService.updateTracking(1L, request);

        assertThat(response).isNotNull();
        verify(progressRepository).save(any(ProgressTracking.class));
    }

    @Test
    void deleteTracking_ShouldCallRepository() {
        progressService.deleteTracking(1L);
        verify(progressRepository).deleteById(1L);
    }
}
