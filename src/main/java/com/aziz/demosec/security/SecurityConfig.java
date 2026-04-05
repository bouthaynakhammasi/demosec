package com.aziz.demosec.security;

import com.aziz.demosec.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/auth/**", "/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/home-care-services/**", "/api/home-care-services").permitAll()
                        .requestMatchers("/api/donations/**", "/api/aid-requests/**", "/api/emergency-alerts/**", "/api/interventions/**", "/api/ambulances/**", "/api/smart-devices/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/clinics/**", "/api/v1/doctors/**").permitAll()
                        
                        // Medical Access
                        .requestMatchers("/treatment/**", "/diagnosis/**", "/consultation/**", "/prescription/**").hasAnyRole("DOCTOR", "NUTRITIONIST")
                        
                        // Role Based
                        .requestMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**", "/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/clinic/**", "/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/api/pharmacist/**", "/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/api/laboratory/**", "/laboratory/**").hasRole("LABORATORYSTAFF")
                        .requestMatchers("/api/nutritionist/**", "/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/api/visitor/**", "/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/api/patient/**", "/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/baby-care/**").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers("/api/home-care/**").hasRole("HOME_CARE_PROVIDER")
                        
                        // Others
                        .requestMatchers("/api/users/role/DOCTOR").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/patients/*/appointments").hasRole("PATIENT")
                        .requestMatchers("/api/v1/doctors/*/appointments").hasRole("DOCTOR")
                        .requestMatchers("/api/v1/appointments/**", "/api/v1/**").authenticated()
                        .requestMatchers("/availability/**", "/provider-calendar/**").hasAnyRole("DOCTOR", "NUTRITIONIST", "HOME_CARE_PROVIDER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
