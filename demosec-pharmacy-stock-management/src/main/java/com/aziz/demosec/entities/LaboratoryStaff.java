package com.aziz.demosec.entities;

import com.aziz.demosec.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "laboratory_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LaboratoryStaff extends User {

    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;

}