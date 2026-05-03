package com.aziz.demosec.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        makeDocterIdNullable();
    }

    private void makeDocterIdNullable() {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE lab_requests MODIFY COLUMN doctor_id BIGINT NULL"
            );
            log.info("✅ lab_requests.doctor_id is now nullable");
        } catch (Exception e) {
            // Column may already be nullable — safe to ignore
            log.debug("doctor_id nullable migration skipped: {}", e.getMessage());
        }
    }
}
