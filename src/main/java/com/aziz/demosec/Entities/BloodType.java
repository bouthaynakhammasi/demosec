package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BloodType {
    A_POS, A_NEG,
    B_POS, B_NEG,
    AB_POS, AB_NEG,
    O_POS, O_NEG;
    @JsonCreator
    public static BloodType from(String value) {
        return BloodType.valueOf(value.toUpperCase());
    }
}