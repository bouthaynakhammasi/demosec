package com.aziz.demosec;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {

        // ── 1. MySQL max_allowed_packet ──────────────────────────────────────
        try {
            jdbcTemplate.execute("SET GLOBAL max_allowed_packet = 134217728");
        } catch (Exception e) {
            System.out.println("[DatabaseFixer] Could not set max_allowed_packet (normal for non-root users)");
        }

        // ── 2. Ensure document_file column exists in aid_requests ────────────
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE aid_requests ADD COLUMN IF NOT EXISTS document_file LONGTEXT"
            );
            System.out.println("✅ document_file column ensured in aid_requests.");
        } catch (Exception e) {
            System.out.println("[DatabaseFixer] document_file already exists or skipped.");
        }

        // ── 3. Migrate supporting_document → document_file ───────────────────
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'aid_requests' " +
                            "AND COLUMN_NAME = 'supporting_document'",
                    Integer.class
            );

            if (count != null && count > 0) {
                jdbcTemplate.execute(
                        "UPDATE aid_requests SET document_file = supporting_document " +
                                "WHERE document_file IS NULL AND supporting_document IS NOT NULL"
                );
                System.out.println("✅ supporting_document migrated to document_file.");
            }

        } catch (Exception e) {
            System.out.println("[DatabaseFixer] Migration skipped.");
        }

        // ── 4. Fix Gender values (IMPORTANT FIX) ─────────────────────────────
        try {
            // Vérifier si table 'patient' existe
            Integer tableExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.TABLES " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'patient'",
                    Integer.class
            );

            if (tableExists != null && tableExists > 0) {

                // Modifier le type de colonne
                jdbcTemplate.execute("ALTER TABLE patient MODIFY COLUMN gender VARCHAR(255)");

                // Corriger les valeurs
                jdbcTemplate.execute("UPDATE patient SET gender = 'MALE' WHERE gender = '0'");
                jdbcTemplate.execute("UPDATE patient SET gender = 'FEMALE' WHERE gender = '1'");

                System.out.println("✅ Gender fixed in patient table.");
            } else {
                System.out.println("[DatabaseFixer] Table 'patient' not found, skipped.");
            }

        } catch (Exception e) {
            System.out.println("[DatabaseFixer] Gender fix skipped safely.");
        }

        // ── 5. Optional: Fix baby_profiles (si existe) ───────────────────────
        try {
            Integer babyTableExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.TABLES " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'baby_profiles'",
                    Integer.class
            );

            if (babyTableExists != null && babyTableExists > 0) {

                jdbcTemplate.execute("ALTER TABLE baby_profiles MODIFY COLUMN gender VARCHAR(255)");
                jdbcTemplate.execute("UPDATE baby_profiles SET gender = 'MALE' WHERE gender = '0'");
                jdbcTemplate.execute("UPDATE baby_profiles SET gender = 'FEMALE' WHERE gender = '1'");

                System.out.println("✅ Gender fixed in baby_profiles.");
            }

        } catch (Exception e) {
            System.out.println("[DatabaseFixer] baby_profiles skipped.");
        }

        // ── END ─────────────────────────────────────────────────────────────
        System.out.println("✅ DatabaseFixer completed successfully.");
    }
}