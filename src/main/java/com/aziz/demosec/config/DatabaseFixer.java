package com.aziz.demosec.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE lab_requests MODIFY doctor_id BIGINT NULL;");
            System.out.println("✅ SUCCESSFULLY ALTERED lab_requests TABLE modifying doctor_id to allow NULL");
        } catch (Exception e) {
            System.err.println("⚠️ Could not alter lab_requests table: " + e.getMessage());
        }
    }
}
