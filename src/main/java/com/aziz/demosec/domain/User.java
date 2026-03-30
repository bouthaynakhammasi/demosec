package com.aziz.demosec.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private Role role;

    private String phone;

    private LocalDate birthDate;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photo;

    // FIX: removed inline defaults, moved to columnDefinition + @PrePersist
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean profileCompleted;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    // FIX: ensures Java-side defaults are set before first DB insert
    @PrePersist
    protected void prePersist() {
        this.enabled = true;
        // profileCompleted stays false by default
    }

    public String getProfilePicture() {
        return profileImage;
    }

    public void setProfilePicture(String profilePicture) {
        this.profileImage = profilePicture;
    }
}