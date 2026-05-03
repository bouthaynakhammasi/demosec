package com.aziz.demosec.service;

import com.aziz.demosec.domain.Role;
import com.aziz.demosec.dto.AnalyticsSummaryResponse;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final MedicalEventRepository eventRepository;

    public AnalyticsSummaryResponse getSummary() {
        long totalUsers = userRepository.count();
        long totalEvents = eventRepository.count();

        // Get users count by role
        Map<String, Long> usersByRole = Arrays.stream(Role.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        role -> userRepository.countByRole(role)
                ));

        // Mock monthly growth for now (Real logic would query by createdAt month)
        List<AnalyticsSummaryResponse.MonthlyGrowth> growth = List.of(
                new AnalyticsSummaryResponse.MonthlyGrowth("Jan", totalUsers / 4),
                new AnalyticsSummaryResponse.MonthlyGrowth("Feb", totalUsers / 3),
                new AnalyticsSummaryResponse.MonthlyGrowth("Mar", totalUsers / 2),
                new AnalyticsSummaryResponse.MonthlyGrowth("Apr", totalUsers)
        );

        return AnalyticsSummaryResponse.builder()
                .totalUsers(totalUsers)
                .totalEvents(totalEvents)
                .totalDonations(48000L) // Mock
                .totalPosts(1250L) // Mock
                .usersByRole(usersByRole)
                .userGrowth(growth)
                .build();
    }
}
