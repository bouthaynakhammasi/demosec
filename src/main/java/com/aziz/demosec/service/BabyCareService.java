package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.Mapper.BabyCareMapper;
import com.aziz.demosec.dto.baby.*;
import com.aziz.demosec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BabyCareService {
    private final BabyProfileRepository babyRepository;
    private final VaccinationRepository vaccineRepository;
    private final JournalEntryRepository journalRepository;
    private final ParentPreferenceRepository preferenceRepository;
    private final PatientRepository patientRepository;
    private final DiaperRecordRepository diaperRepository;
    private final BabyCareMapper mapper;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional
    public BabyProfileResponseDTO createProfile(Long parentId, BabyProfileRequestDTO dto) {
        Patient parent = patientRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        BabyProfile baby = BabyProfile.builder()
                .parent(parent)
                .name(dto.getName())
                .birthDate(dto.getBirthDate())
                .gender(Gender.valueOf(dto.getGender()))
                .birthWeight(dto.getBirthWeight())
                .birthHeight(dto.getBirthHeight())
                .photoUrl(dto.getPhotoUrl())
                .build();

        BabyProfile saved = babyRepository.save(baby);

        if (dto.getPriorities() != null) {
            List<ParentPreference> prefs = dto.getPriorities().stream()
                    .map(p -> ParentPreference.builder()
                            .babyProfile(saved)
                            .priorityType(p)
                            .selected(true)
                            .build())
                    .collect(Collectors.toList());
            preferenceRepository.saveAll(prefs);
        }

        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public BabyProfileResponseDTO getProfileByPatientId(Long patientId) {
        List<BabyProfile> profiles = babyRepository.findByParentId(patientId);
        if (profiles.isEmpty()) return null;
        return mapper.toResponseDTO(profiles.get(0));
    }

    public BabyDashboardDTO getDashboard(Long babyId) {
        BabyProfile baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        long ageInDays = java.time.temporal.ChronoUnit.DAYS.between(baby.getBirthDate(), LocalDate.now());
        String ageDisplay = calculateAge(baby.getBirthDate());

        // Dynamic tip based on age
        String dailyTip = getDynamicTip(ageInDays);

        // Vaccinations: Filter real records from DB
        List<Vaccination> records = vaccineRepository.findByBabyId(babyId);
        List<VaccineSummaryDTO> administered = records.stream()
                .map(mapper::toVaccineResponseDTO)
                .collect(Collectors.toList());

        List<JournalEntry> journal = journalRepository.findByBabyProfileIdOrderByCreatedAtDesc(babyId);
        List<JournalSummaryDTO> recentLogs = journal.stream().limit(3)
                .map(j -> JournalSummaryDTO.builder()
                        .type(j.getEntryType().toString())
                        .content(j.getValue())
                        .time(j.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        
        // Sleep stats (unified seconds aggregation)
        long totalSecondsToday = journal.stream()
                .filter(j -> j.getEntryType() == JournalEntryType.SLEEP && j.getCreatedAt().toLocalDate().equals(today))
                .mapToLong(j -> {
                    try {
                        // 1. Priority: Extract from metadata JSON for perfect precision
                        if (j.getMetadata() != null && !j.getMetadata().isEmpty()) {
                            try {
                                com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(j.getMetadata());
                                if (node.has("totalDurationSeconds")) {
                                    return node.get("totalDurationSeconds").asLong();
                                }
                            } catch (Exception e) { /* ignore and fallback */ }
                        }

                        // 2. Fallback: Parse string description
                        String val = j.getValue();
                        if (val == null) return 0;
                        
                        java.util.regex.Matcher mHr = java.util.regex.Pattern.compile("(\\d+)\\s*h").matcher(val);
                        java.util.regex.Matcher mMin = java.util.regex.Pattern.compile("(\\d+)\\s*min").matcher(val);
                        java.util.regex.Matcher mSec = java.util.regex.Pattern.compile("(\\d+)\\s*sec").matcher(val);
                        
                        long totalS = 0;
                        if (mHr.find()) totalS += Long.parseLong(mHr.group(1)) * 3600;
                        if (mMin.find()) totalS += Long.parseLong(mMin.group(1)) * 60;
                        if (mSec.find()) totalS += Long.parseLong(mSec.group(1));
                        
                        if (totalS == 0) {
                             java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(val);
                             if (m.find()) totalS = Long.parseLong(m.group()) * 60;
                        }
                        return totalS;
                    } catch (Exception e) { return 0; }
                }).sum();

        int totalSleepToday = (int) (totalSecondsToday / 60); 

        // Diaper stats
        List<DiaperRecord> diapers = diaperRepository.findByBabyProfileIdOrderByChangedAtDesc(babyId);
        List<DiaperRecord> diapersToday = diapers.stream()
                .filter(d -> d.getChangedAt().toLocalDate().equals(today))
                .collect(Collectors.toList());

        int totalDiapersToday = diapersToday.size();
        int wetDiapersToday = (int) diapersToday.stream().filter(d -> d.getDiaperType() == DiaperType.WET || d.getDiaperType() == DiaperType.MIXED).count();
        int dirtyDiapersToday = (int) diapersToday.stream().filter(d -> d.getDiaperType() == DiaperType.DIRTY || d.getDiaperType() == DiaperType.MIXED).count();

        // Milestone progress: Dynamic calculation (e.g., % of expected milestones achieved for age)
        double progress = calculateMilestoneProgress(ageInDays);

        // Weekly Sleep Data
        List<SleepDayDTO> weeklySleep = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            long secs = journal.stream()
                .filter(j -> j.getEntryType() == JournalEntryType.SLEEP && j.getCreatedAt().toLocalDate().equals(d))
                .mapToLong(j -> {
                    try {
                        if (j.getMetadata() != null && !j.getMetadata().isEmpty()) {
                            try {
                                com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(j.getMetadata());
                                if (node.has("totalDurationSeconds")) return node.get("totalDurationSeconds").asLong();
                            } catch (Exception e) {}
                        }
                        String val = j.getValue();
                        if (val == null) return 0;
                        java.util.regex.Matcher mHr = java.util.regex.Pattern.compile("(\\d+)\\s*h").matcher(val);
                        java.util.regex.Matcher mMin = java.util.regex.Pattern.compile("(\\d+)\\s*min").matcher(val);
                        java.util.regex.Matcher mSec = java.util.regex.Pattern.compile("(\\d+)\\s*sec").matcher(val);
                        
                        long s = 0;
                        if (mHr.find()) s += Long.parseLong(mHr.group(1)) * 3600;
                        if (mMin.find()) s += Long.parseLong(mMin.group(1)) * 60;
                        if (mSec.find()) s += Long.parseLong(mSec.group(1));
                        
                        if (s == 0) {
                            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(val);
                            if (m.find()) s = Long.parseLong(m.group()) * 60;
                        }
                        return s;
                    } catch (Exception e) { return 0; }
                }).sum();
            weeklySleep.add(new SleepDayDTO(d.getDayOfWeek().toString().substring(0, 3), secs));
        }

        return BabyDashboardDTO.builder()
                .id(baby.getId())
                .name(baby.getName())
                .age(ageDisplay)
                .weightAtBirth(baby.getBirthWeight())
                .heightAtBirth(baby.getBirthHeight())
                .dailyTip(dailyTip)
                .upcomingVaccines(administered)
                .recentLogs(recentLogs)
                .milestoneProgress(progress)
                .totalSleepSecondsToday((int)totalSecondsToday)
                .weeklySleep(weeklySleep)
                .diaperTotalToday(totalDiapersToday)
                .diaperWetToday(wetDiapersToday)
                .diaperDirtyToday(dirtyDiapersToday)
                .build();
    }

    public VaccinationOverviewDTO getVaccinationOverview(Long babyId) {
        BabyProfile baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        LocalDate birthDate = baby.getBirthDate();
        LocalDate now = LocalDate.now();
        List<Vaccination> administeredRecords = vaccineRepository.findByBabyId(babyId);
        
        List<VaccineScheduleItem> schedule = getFullVaccineSchedule();
        
        List<VaccineSummaryDTO> done = administeredRecords.stream()
                .map(mapper::toVaccineResponseDTO)
                .collect(Collectors.toList());
        
        List<VaccineSummaryDTO> dueNow = new java.util.ArrayList<>();
        List<VaccineSummaryDTO> overdue = new java.util.ArrayList<>();
        List<VaccineSummaryDTO> upcoming = new java.util.ArrayList<>();
        
        for (VaccineScheduleItem item : schedule) {
            boolean isDone = administeredRecords.stream()
                    .anyMatch(r -> r.getVaccineName().equalsIgnoreCase(item.getName()));
            
            if (isDone) continue;
            
            LocalDate targetDate = birthDate.plusMonths(item.getMilestoneMonths());
            VaccineSummaryDTO dto = VaccineSummaryDTO.builder()
                    .name(item.getName())
                    .dueDate(targetDate)
                    .description(item.getDescription())
                    .build();
            
            if (now.isAfter(targetDate.plusDays(7))) {
                dto.setStatus("OVERDUE");
                overdue.add(dto);
            } else if (now.isAfter(targetDate.minusDays(7)) || now.isEqual(targetDate)) {
                dto.setStatus("DUE");
                dueNow.add(dto);
            } else {
                dto.setStatus("UPCOMING");
                upcoming.add(dto);
            }
        }
        
        double progress = (double) done.size() / (done.size() + dueNow.size() + overdue.size() + upcoming.size()) * 100;
        
        VaccineSummaryDTO next = !dueNow.isEmpty() ? dueNow.get(0) : (!upcoming.isEmpty() ? upcoming.get(0) : null);
        
        return VaccinationOverviewDTO.builder()
                .progressPercent(Math.round(progress))
                .summaryMessage(generateVaccineSummaryMessage(baby.getName(), overdue.size(), dueNow.size()))
                .nextVaccine(next)
                .done(done)
                .dueNow(dueNow)
                .overdue(overdue)
                .upcoming(upcoming)
                .build();
    }

    private String generateVaccineSummaryMessage(String name, int overdue, int due) {
        if (overdue > 0) return name + " has " + overdue + " overdue vaccinations. Please schedule an appointment.";
        if (due > 0) return name + " has vaccinations due now.";
        return name + " is all caught up with vaccinations!";
    }

    private List<VaccineScheduleItem> getFullVaccineSchedule() {
        return List.of(
            new VaccineScheduleItem("BCG", 0, "Tuberculosis protection"),
            new VaccineScheduleItem("Hepatitis B", 0, "Initial liver protection"),
            new VaccineScheduleItem("Polio (OPV 1)", 2, "Polio protection dose 1"),
            new VaccineScheduleItem("DTP 1", 2, "Diphtheria, Tented, Pertussis dose 1"),
            new VaccineScheduleItem("Hib 1", 2, "Influenza type B protection dose 1"),
            new VaccineScheduleItem("Hepatitis B 2", 2, "Hepatitis B dose 2"),
            new VaccineScheduleItem("Polio (OPV 2)", 4, "Polio protection dose 2"),
            new VaccineScheduleItem("DTP 2", 4, "Diphtheria, Tented, Pertussis dose 2"),
            new VaccineScheduleItem("Hib 2", 4, "Influenza type B protection dose 2")
        );
    }

    @lombok.Value
    private static class VaccineScheduleItem {
        String name;
        int milestoneMonths;
        String description;
    }

    @Transactional
    public VaccineSummaryDTO addVaccineRecord(Long babyId, String vaccineName, LocalDate date) {
        BabyProfile baby = babyRepository.findById(babyId).orElseThrow();
        
        String description = getFullVaccineSchedule().stream()
                .filter(i -> i.getName().equalsIgnoreCase(vaccineName))
                .map(VaccineScheduleItem::getDescription)
                .findFirst()
                .orElse("Vaccination completion record");

        Vaccination record = Vaccination.builder()
                .baby(baby)
                .vaccineName(vaccineName)
                .administeredDate(date)
                .description(description)
                .dueDate(date) // Satisfy legacy NOT NULL constraint
                .status("DONE") // Satisfy legacy NOT NULL constraint
                .build();
        return mapper.toVaccineResponseDTO(vaccineRepository.save(record));
    }

    @Transactional
    public void deleteVaccineRecord(Long babyId, String vaccineName) {
        List<Vaccination> records = vaccineRepository.findByBabyIdAndVaccineName(babyId, vaccineName);
        if (!records.isEmpty()) {
            vaccineRepository.deleteAll(records);
        }
    }

    public List<JournalEntryResponseDTO> getJournal(Long babyId) {
        return journalRepository.findByBabyProfileIdOrderByCreatedAtDesc(babyId).stream()
                .map(mapper::toJournalResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public JournalEntryResponseDTO addJournalEntry(Long babyId, JournalEntryType type, String value, String notes, String metadata) {
        BabyProfile baby = babyRepository.findById(babyId).orElseThrow();
        JournalEntry entry = JournalEntry.builder()
                .babyProfile(baby)
                .entryType(type)
                .value(value)
                .notes(notes)
                .metadata(metadata)
                .build();
        
        // Protection against unrealistic durations (Max 24h)
        if (type == JournalEntryType.SLEEP) {
            try {
                if (metadata != null && !metadata.isEmpty()) {
                    com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(metadata);
                    if (node.has("totalDurationSeconds")) {
                        long secs = node.get("totalDurationSeconds").asLong();
                        if (secs < 0 || secs > 172800) { // Just ensure it's not negative or absurd
                            throw new RuntimeException("La durée de sommeil est invalide.");
                        }
                    }
                }
            } catch (Exception e) {
                if (e instanceof RuntimeException) throw (RuntimeException) e;
            }
        }
        
        return mapper.toJournalResponseDTO(journalRepository.save(entry));
    }

    @Transactional
    public void deleteJournalEntry(Long id) {
        journalRepository.deleteById(id);
    }

    // DIAPER METHODS
    public List<DiaperRecordDTO> getDiapers(Long babyId) {
        return diaperRepository.findByBabyProfileIdOrderByChangedAtDesc(babyId).stream()
                .map(mapper::toDiaperRecordDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiaperRecordDTO addDiaper(Long babyId, DiaperRecordDTO dto) {
        BabyProfile baby = babyRepository.findById(babyId).orElseThrow();
        DiaperRecord record = DiaperRecord.builder()
                .babyProfile(baby)
                .diaperType(DiaperType.valueOf(dto.getDiaperType()))
                .rashNoted(dto.isRashNoted())
                .stoolColor(dto.getStoolColor())
                .stoolTexture(dto.getStoolTexture())
                .notes(dto.getNotes())
                .changedAt(dto.getChangedAt() != null ? dto.getChangedAt() : LocalDateTime.now())
                .build();
        return mapper.toDiaperRecordDTO(diaperRepository.save(record));
    }

    @Transactional
    public DiaperRecordDTO updateDiaper(Long id, DiaperRecordDTO dto) {
        DiaperRecord record = diaperRepository.findById(id).orElseThrow();
        record.setDiaperType(DiaperType.valueOf(dto.getDiaperType()));
        record.setRashNoted(dto.isRashNoted());
        record.setStoolColor(dto.getStoolColor());
        record.setStoolTexture(dto.getStoolTexture());
        record.setNotes(dto.getNotes());
        if (dto.getChangedAt() != null) record.setChangedAt(dto.getChangedAt());
        return mapper.toDiaperRecordDTO(diaperRepository.save(record));
    }

    @Transactional
    public void deleteDiaper(Long id) {
        diaperRepository.deleteById(id);
    }

    private String calculateAge(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        long days = java.time.temporal.ChronoUnit.DAYS.between(birthDate, now);
        
        if (days < 30) {
            return days + " days";
        }
        
        Period period = Period.between(birthDate, now);
        if (period.getYears() > 0) {
            return period.getYears() + "y " + period.getMonths() + "m";
        }
        return period.getMonths() + " months";
    }

    private String getDynamicTip(long ageInDays) {
        if (ageInDays < 30) return "Tummy time is important from week one!";
        if (ageInDays < 180) return "Your baby is starting to recognize faces!";
        return "Time to explore solid foods slowly.";
    }

    private double calculateMilestoneProgress(long ageInDays) {
        // Simplified dynamic calculation
        if (ageInDays < 30) return 15.0;
        if (ageInDays < 90) return 35.0;
        if (ageInDays < 180) return 60.0;
        return 85.0;
    }
}

