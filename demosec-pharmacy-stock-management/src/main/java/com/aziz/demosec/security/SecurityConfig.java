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
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    // 🔥 AJOUT IMPORTANT
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public DaoAuthenticationProvider authProvider() {
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())

                // 🔥 CONFIG OAUTH2 GOOGLE
                .oauth2Login(o -> o.successHandler(oAuth2LoginSuccessHandler))

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll() // 🔥 IMPORTANT
                        .requestMatchers("/login/**").permitAll() // 🔥 IMPORTANT
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/home-care-services/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // Doctor medical module
                        .requestMatchers("/treatment/**").hasRole("DOCTOR")
                        .requestMatchers("/diagnosis/**").hasRole("DOCTOR")
                        .requestMatchers("/consultation/**").hasRole("DOCTOR")
                        .requestMatchers("/prescription/**").hasRole("DOCTOR")

                        // Other roles
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "PATIENT", "PHARMACIST")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/laboratory/**").hasRole("LABORATORYSTAFF")
                        .requestMatchers("/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/events/*/participate").authenticated()
                        .requestMatchers("/api/events/*/cancel-participation").authenticated()
                        .requestMatchers("/api/events/*/is-participating").authenticated()
                        .requestMatchers("/api/events/public/**").permitAll()

                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}