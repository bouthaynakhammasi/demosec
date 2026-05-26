package com.aziz.demosec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {
    // Configuration de test pour isoler les problèmes
}
