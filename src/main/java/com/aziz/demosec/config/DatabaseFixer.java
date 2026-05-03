package com.aziz.demosec.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseFixer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFixer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("Running DB constraint fixes for legacy SINGLE_TABLE schema...");
        String[] queries = {
            "ALTER TABLE users MODIFY COLUMN license_number VARCHAR(255) NULL;",
            "ALTER TABLE users MODIFY COLUMN pharmacy_setup_completed BIT NULL;",
            "ALTER TABLE users MODIFY COLUMN status VARCHAR(255) NULL;",
            "ALTER TABLE users MODIFY COLUMN diploma_document LONGTEXT NULL;",
            "ALTER TABLE users MODIFY COLUMN verified BIT NULL;",
            "ALTER TABLE users MODIFY COLUMN registration_number VARCHAR(255) NULL;",
            "ALTER TABLE users MODIFY COLUMN facility_type VARCHAR(255) NULL;",
            "ALTER TABLE event_seats MODIFY COLUMN posx DOUBLE NULL;",
            "ALTER TABLE event_seats MODIFY COLUMN posy DOUBLE NULL;",
            "ALTER TABLE event_seats MODIFY COLUMN pos_x DOUBLE NULL;",
            "ALTER TABLE event_seats MODIFY COLUMN pos_y DOUBLE NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_name VARCHAR(255) NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_address VARCHAR(255) NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_phone VARCHAR(255) NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_email VARCHAR(255) NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_setup_completed BIT DEFAULT 0 NULL;",
            "ALTER TABLE users ADD COLUMN status VARCHAR(50) DEFAULT 'PENDING' NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_id BIGINT NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_latitude FLOAT NULL;",
            "ALTER TABLE users ADD COLUMN pharmacy_longitude FLOAT NULL;",
            "UPDATE users SET dtype = 'Patient' WHERE role = 'PATIENT' AND (dtype IS NULL OR dtype = '' OR dtype = 'PATIENT');",
            "UPDATE users SET dtype = 'Pharmacist' WHERE role = 'PHARMACIST' AND (dtype IS NULL OR dtype = '' OR dtype = 'PHARMACIST');",
            "UPDATE users SET dtype = 'Doctor' WHERE role = 'DOCTOR' AND (dtype IS NULL OR dtype = '' OR dtype = 'DOCTOR');",
            "UPDATE users SET dtype = 'LaboratoryStaff' WHERE role = 'LABORATORY_STAFF' AND (dtype IS NULL OR dtype = '' OR dtype = 'LABORATORY_STAFF');",
            "UPDATE users SET dtype = 'Nutritionist' WHERE role = 'NUTRITIONIST' AND (dtype IS NULL OR dtype = '' OR dtype = 'NUTRITIONIST');",
            "UPDATE users SET dtype = 'User' WHERE dtype IS NULL OR dtype = '' OR dtype = 'USER';",
            "INSERT INTO home_care_services (name, description, price) SELECT 'Nursing Care', 'Professional nursing services at home.', 50.00 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM home_care_services LIMIT 1);",
            "INSERT INTO home_care_services (name, description, price) SELECT 'Physical Therapy', 'Rehabilitative therapy in the comfort of your home.', 75.00 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM home_care_services WHERE name = 'Physical Therapy');",
            "INSERT INTO home_care_services (name, description, price) SELECT 'Elderly Support', 'Daily assistance for senior citizens.', 40.00 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM home_care_services WHERE name = 'Elderly Support');"
        };
        for (String q : queries) {
            try {
                jdbcTemplate.execute(q);
                log.info("Executed: {}", q);
            } catch (Exception e) {
                log.debug("Skipped: {} due to {}", q, e.getMessage());
            }
        }
    }
}
