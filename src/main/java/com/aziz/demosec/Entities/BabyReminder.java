package com.aziz.demosec.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "baby_reminders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BabyReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_profile_id", nullable = false)
    private BabyProfile babyProfile;

    private String reminderType; // VACCINE, CHECKUP, FEEDING
    private String title;
    private LocalDateTime dueDate;
    
    private boolean status; // completed or not
    private boolean notificationEnabled;
}
