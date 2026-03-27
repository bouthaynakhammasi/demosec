package com.aziz.demosec.domain;

public enum Role {
    ADMIN,
    DOCTOR,
    CLINIC,
    PHARMACIST,
    // ⚠️ Typo: missing 'T' — do not rename (DB migration risk)
    LABORATORYSAFF,
    NUTRITIONIST,
    VISITOR,
    PATIENT,
    HOME_CARE_PROVIDER
}
