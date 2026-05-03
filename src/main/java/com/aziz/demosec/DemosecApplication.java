package com.aziz.demosec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemosecApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemosecApplication.class, args);
    }

}
