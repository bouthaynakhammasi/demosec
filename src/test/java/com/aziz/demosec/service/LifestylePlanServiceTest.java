package com.aziz.demosec.service;

import com.aziz.demosec.Entities.LifestyleGoal;
import com.aziz.demosec.Entities.LifestylePlan;
import com.aziz.demosec.Entities.Nutritionist;
import com.aziz.demosec.Entities.PlanStatus;
import com.aziz.demosec.dto.LifestylePlanRequest;
import com.aziz.demosec.dto.LifestylePlanResponse;
import com.aziz.demosec.repository.LifestyleGoalRepository;
import com.aziz.demosec.repository.LifestylePlanRepository;
import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LifestylePlanServiceTest {

    @Mock
    private LifestylePlanRepository planRepository;

    @Mock
    private LifestyleGoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LifestylePlanService planService;

    private LifestyleGoal goal;
    private Nutritionist nutritionist;
    private LifestylePlan plan;
    private LifestylePlanRequest request;

    @BeforeEach
    void setUp() {
        goal = TestDataFactory.createLifestyleGoal(1L, null);
        nutritionist = TestDataFactory.createNutritionist(2L, "nutritionist@test.com");
        plan = TestDataFactory.createLifestylePlan(1L, goal, nutritionist);

        request = LifestylePlanRequest.builder()
                .goalId(1L)
                .nutritionistId(2L)
                .title("Updated Plan")
                .description("Updated Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .status("ACTIVE")
                .build();
    }

    @Test
    void addPlan_ShouldReturnResponse_WhenValid() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(nutritionist));
        when(planRepository.save(any(LifestylePlan.class))).thenReturn(plan);

        // Act
        LifestylePlanResponse response = planService.addPlan(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(plan.getId());
        verify(planRepository).save(any(LifestylePlan.class));
    }

    @Test
    void addPlan_ShouldThrowException_WhenGoalNotFound() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> planService.addPlan(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Goal not found");
    }

    @Test
    void getPlanById_ShouldReturnResponse_WhenFound() {
        // Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // Act
        LifestylePlanResponse response = planService.getPlanById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(plan.getId());
    }

    @Test
    void updatePlan_ShouldReturnUpdatedResponse() {
        // Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.save(any(LifestylePlan.class))).thenReturn(plan);

        // Act
        LifestylePlanResponse response = planService.updatePlan(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(planRepository).save(any(LifestylePlan.class));
    }

    @Test
    void deletePlan_ShouldCallRepository() {
        // Act
        planService.deletePlan(1L);

        // Assert
        verify(planRepository).deleteById(1L);
    }
}
