package com.aziz.demosec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling   // ← Active les @Scheduled dans tout le projet

@SpringBootApplication
public class DemosecApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemosecApplication.class, args);
    }

}
