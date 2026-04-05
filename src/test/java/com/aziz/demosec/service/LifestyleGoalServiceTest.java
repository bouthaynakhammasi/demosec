package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.dto.LifestyleGoalRequest;
import com.aziz.demosec.dto.LifestyleGoalResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LifestyleGoalServiceTest {

    @Mock
    private LifestyleGoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LifestyleGoalService goalService;

    private Patient patient;
    private LifestyleGoal goal;
    private LifestyleGoalRequest request;

    @BeforeEach
    void setUp() {
        patient = TestDataFactory.createPatient(1L, "patient@test.com");
        goal = TestDataFactory.createLifestyleGoal(1L, patient);
        
        request = LifestyleGoalRequest.builder()
                .patientId(1L)
                .category("WEIGHT_LOSS")
                .targetValue(new BigDecimal("80.0"))
                .baselineValue(new BigDecimal("90.0"))
                .targetDate(LocalDate.now().plusMonths(2))
                .build();
    }

    @Test
    void addGoal_ShouldReturnResponse_WhenValid() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(goalRepository.save(any(LifestyleGoal.class))).thenReturn(goal);

        // Act
        LifestyleGoalResponse response = goalService.addGoal(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(goal.getId());
        verify(userRepository).findById(1L);
        verify(goalRepository).save(any(LifestyleGoal.class));
    }

    @Test
    void addGoal_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> goalService.addGoal(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
        
        verify(goalRepository, never()).save(any());
    }

    @Test
    void getGoalById_ShouldReturnResponse_WhenFound() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act
        LifestyleGoalResponse response = goalService.getGoalById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(goal.getId());
    }

    @Test
    void getGoalById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> goalService.getGoalById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Goal not found");
    }

    @Test
    void getAllGoals_ShouldReturnList() {
        // Arrange
        when(goalRepository.findAll()).thenReturn(List.of(goal));

        // Act
        List<LifestyleGoalResponse> responses = goalService.getAllGoals();

        // Assert
        assertThat(responses).hasSize(1);
    }

    @Test
    void updateGoal_ShouldReturnUpdatedResponse() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(LifestyleGoal.class))).thenReturn(goal);

        // Act
        LifestyleGoalResponse response = goalService.updateGoal(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(goalRepository).save(any(LifestyleGoal.class));
    }

    @Test
    void deleteGoal_ShouldCallRepository() {
        // Act
        goalService.deleteGoal(1L);

        // Assert
        verify(goalRepository).deleteById(1L);
    }
}
