package com.aziz.demosec.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
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

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean profileCompleted;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    private String professionalDocument;

    @PrePersist
    protected void prePersist() {
        if (this.role != Role.ADMIN) {
             // For some roles like Pharmacist or Provider, they might be disabled until approved
             // But if specific logic was there like 'enabled = true', I'll default to true unless specified.
        }
        // Default to true as per previous implementation but can be overridden by subclasses or logic.
        if (this.email != null) {
            // Keep existing enabled value if already set (e.g. by builder)
        }
    }

    public String getProfilePicture() {
        return profileImage;
    }

    public void setProfilePicture(String profilePicture) {
        this.profileImage = profilePicture;
    }
}
