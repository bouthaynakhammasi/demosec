package com.aziz.demosec.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRequestDTO {

    private Double age;
    private String gender;

    @JsonProperty("chronic_diseases")
    private String chronicDiseases;

    @JsonProperty("hereditary_diseases")
    private Integer hereditaryDiseases;

    @JsonProperty("drug_allergies")
    private Integer drugAllergies;

    @JsonProperty("medical_record_exists")
    private Integer medicalRecordExists;

    @JsonProperty("nb_diagnoses")
    private Integer nbDiagnoses;

    @JsonProperty("diagnosis_type")
    private String diagnosisType;

    @JsonProperty("nb_prescriptions")
    private Integer nbPrescriptions;

    @JsonProperty("nb_prescription_items")
    private Integer nbPrescriptionItems;

    @JsonProperty("revenus_mensuels_tnd")
    private Double revenusMenuelsTnd;

    @JsonProperty("personnes_a_charge")
    private Integer personnesACharge;

    @JsonProperty("situation_professionnelle")
    private String situationProfessionnelle;

    @JsonProperty("score_precarite")
    private Double scorePrecarite;

    @JsonProperty("nb_dons_recus")
    private Integer nbDonsRecus;

    @JsonProperty("montant_dons_tnd")
    private Double montantDonsTnd;

    @JsonProperty("donation_status")
    private String donationStatus;
}
