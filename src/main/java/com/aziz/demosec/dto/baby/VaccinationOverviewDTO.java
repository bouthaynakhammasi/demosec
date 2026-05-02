package com.aziz.demosec.dto.baby;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class VaccinationOverviewDTO {
    private double progressPercent;
    private String summaryMessage;
    private VaccineSummaryDTO nextVaccine;
    private List<VaccineSummaryDTO> overdue;
    private List<VaccineSummaryDTO> dueNow;
    private List<VaccineSummaryDTO> upcoming;
    private List<VaccineSummaryDTO> done;
}
