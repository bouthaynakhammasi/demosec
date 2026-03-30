package com.aziz.demosec.dto.baby;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BabyDashboardDTO {
    private Long id;
    private String name;
    private String age;
    private Double weightAtBirth;
    private Double heightAtBirth;
    private String photoUrl;
    private String nextCheckupDate;
    private String dailyTip;
    
    private List<VaccineSummaryDTO> upcomingVaccines;
    private List<JournalSummaryDTO> recentLogs;
    private Double milestoneProgress;
    private int totalSleepSecondsToday;
    private List<SleepDayDTO> weeklySleep; 

    private Integer diaperTotalToday;
    private Integer diaperWetToday;
    private Integer diaperDirtyToday;
}
