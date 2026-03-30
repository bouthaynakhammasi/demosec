package com.aziz.demosec.security;

import com.aziz.demosec.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth

                        // ─── OPTIONS PREFLIGHT ─────────────────────────────────────
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ─── PUBLIC ────────────────────────────────────────────────
                        .requestMatchers("/auth/**", "/error/**", "/uploads/**").permitAll()
                        .requestMatchers("/api/home-care-services/**").permitAll()
                        .requestMatchers("/api/donations/**", "/api/aid-requests/**").permitAll()
                        .requestMatchers("/api/emergency-alerts/**", "/api/interventions/**").permitAll()
                        .requestMatchers("/api/ambulances/**", "/api/smart-devices/**").permitAll()
                        .requestMatchers("/api/laboratories/**").permitAll()

                        // ─── FORUM ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/forum/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/forum/posts/**").hasAnyRole(
                                "DOCTOR", "CLINIC", "PHARMACIST",
                                "LABORATORY_STAFF", "NUTRITIONIST",
                                "HOME_CARE_PROVIDER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/forum/posts/**").hasAnyRole(
                                "DOCTOR", "CLINIC", "PHARMACIST",
                                "LABORATORY_STAFF", "NUTRITIONIST",
                                "HOME_CARE_PROVIDER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/forum/posts/**").hasAnyRole(
                                "DOCTOR", "CLINIC", "PHARMACIST",
                                "LABORATORY_STAFF", "NUTRITIONIST",
                                "HOME_CARE_PROVIDER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/forum/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/forum/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/forum/comments/**").authenticated()
                        .requestMatchers("/api/forum/posts/*/like").authenticated()

                        // ─── SHARED AUTHENTICATED ───────────────────────────────────
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/v1/doctors/**").authenticated()
                        .requestMatchers("/api/clinics/**").permitAll()

                        // ─── MEDICAL ROLES ─────────────────────────────────────────
                        .requestMatchers("/treatment/**", "/diagnosis/**",
                                "/consultation/**", "/prescription/**")
                        .hasAnyRole("DOCTOR", "NUTRITIONIST")

                        // ─── ROLE-BASED (api/ prefix) ───────────────────────────────
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**", "/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/clinic/**", "/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/api/pharmacist/**", "/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/api/laboratory/**", "/laboratory/**").hasRole("LABORATORY_STAFF")
                        .requestMatchers("/api/nutritionist/**", "/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/api/visitor/**", "/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/api/patient/**", "/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/home-care/**").hasRole("HOME_CARE_PROVIDER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}