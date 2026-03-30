package com.aziz.demosec.Entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor extends User {
    private String specialty;
    
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;
    
    private BigDecimal consultationFee;
    
    @Enumerated(EnumType.STRING)
    private ConsultationMode consultationMode;
    
    private String clinicAddress;
 
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "slot_duration")
    private Integer slotDuration = 30;
 
    private Integer patientCount = 0;
 
    private Double rating = 0.0;
    
    private Integer yearsOfExperience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Delegate for profilePicture which is stored in User.profileImage
    public String getProfilePicture() {
        return getProfileImage();
    }
    
    public void setProfilePicture(String profilePicture) {
        setProfileImage(profilePicture);
    }
}
