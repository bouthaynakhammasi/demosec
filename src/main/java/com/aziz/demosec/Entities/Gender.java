package com.aziz.demosec.Entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
<<<<<<< HEAD
    FEMALE;

    @JsonCreator
    public static Gender from(String value) {
        if (value == null) return null;
        // Handle legacy ordinal values stored as "0" or "1"
        if (value.equals("0")) return MALE;
        if (value.equals("1")) return FEMALE;
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Unknown value — don't crash
        }
    }
}
=======
    FEMALE,
    OTHER
}
>>>>>>> origin/MedicalRecord
