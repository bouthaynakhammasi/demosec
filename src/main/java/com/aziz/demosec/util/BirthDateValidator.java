package com.aziz.demosec.util;

import java.time.LocalDate;
import java.time.Period;

/**
 * Utilitaire pour la validation de la date de naissance
 */
public class BirthDateValidator {

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;

    /**
     * Valide une date de naissance selon les règles métier
     * 
     * @param birthDate Date de naissance à valider
     * @return true si valide, false sinon
     * @throws IllegalArgumentException si la date est invalide avec un message
     *                                  descriptif
     */
    public static void validate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }

        LocalDate today = LocalDate.now();

        // Vérifier que la date n'est pas dans le futur
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur");
        }

        // Calculer l'âge
        int age = Period.between(birthDate, today).getYears();

        // Vérifier l'âge minimum
        if (age < MIN_AGE) {
            throw new IllegalArgumentException(
                    String.format("Vous devez avoir au moins %d ans (vous avez actuellement %d ans)", MIN_AGE, age));
        }

        // Vérifier l'âge maximum (plausibilité)
        if (age > MAX_AGE) {
            throw new IllegalArgumentException(
                    String.format("La date de naissance semble invalide. Âge maximal accepté: %d ans", MAX_AGE));
        }
    }

    /**
     * Calcule l'âge exact à partir d'une date de naissance
     * 
     * @param birthDate Date de naissance
     * @return L'âge en années
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Obtient la date limite pour avoir au moins 18 ans
     * 
     * @return Date limite (aujourd'hui - 18 ans)
     */
    public static LocalDate getMaxBirthDateFor18Years() {
        return LocalDate.now().minusYears(MIN_AGE);
    }

    /**
     * Vérifie si une date de naissance est valide pour un patient
     * 
     * @param birthDate Date à vérifier
     * @return true si valide, false sinon
     */
    public static boolean isValidBirthDate(LocalDate birthDate) {
        try {
            validate(birthDate);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
