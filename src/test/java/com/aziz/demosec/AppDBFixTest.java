package com.aziz.demosec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class AppDBFixTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void fixDb() {
        String[] queries = {
            "ALTER TABLE users MODIFY COLUMN license_number VARCHAR(255) NULL",
            "ALTER TABLE users MODIFY COLUMN pharmacy_setup_completed BIT NULL",
            "ALTER TABLE users MODIFY COLUMN status VARCHAR(255) NULL",
            "ALTER TABLE users MODIFY COLUMN diploma_document TEXT NULL"
        };
        for (String q : queries) {
            try {
                jdbcTemplate.execute(q);
                System.out.println("Executed: " + q);
            } catch (Exception e) {
                System.out.println("Skipped: " + q + " due to " + e.getMessage());
            }
        }
    }
}
