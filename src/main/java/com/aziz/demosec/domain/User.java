package com.aziz.demosec.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
    private boolean enabled = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean profileCompleted;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    private String professionalDocument;

    @PrePersist
    protected void prePersist() {
        // Just in case
    }

    public String getProfilePicture() {
        return profileImage;
    }

    public void setProfilePicture(String profilePicture) {
        this.profileImage = profilePicture;
    }
}
