package com.aziz.demosec.security;

import com.aziz.demosec.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

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
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; " +
                                        "img-src 'self' data: http://localhost:8081; " +
                                        "connect-src 'self' http://localhost:8081 ws://localhost:8081 http://localhost:4200;")
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ Endpoints publics (register + login + uploads + websocket + homecare catalogue)
                        .requestMatchers("/api/auth/**", "/auth/**", "/api/upload/**", "/uploads/**",
                                         "/ws/**", "/api/pharmacy/orders/*/invoice",
                                         "/api/pharmacy/deliveries/**",
                                         "/api/homecare/services",
                                         "/api/homecare/services/**",
                                         "/api/homecare/providers/**").permitAll()
                        // ✅ Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/home-care-services/**").permitAll()
                        .requestMatchers("/user/**").authenticated()  // ✅ ajoute cette ligne

                        // ✅ Endpoints protégés par rôle
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/api/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/api/laboratory/**").hasRole("LABORATORY_STAFF")
                        .requestMatchers("/api/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/api/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/api/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/homecare/provider/**").hasRole("HOME_CARE_PROVIDER")
                        // Doctor medical module
                        .requestMatchers("/treatment/**").hasRole("DOCTOR")
                        .requestMatchers("/diagnosis/**").hasRole("DOCTOR")
                        .requestMatchers("/consultation/**").hasRole("DOCTOR")
                        .requestMatchers("/prescription/**").hasRole("DOCTOR")

                        // ✅ Accès spécifique aux commandes de pharmacie
                        .requestMatchers("/api/pharmacy/orders/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/pharmacy/orders/pharmacy/**").hasRole("PHARMACIST")
                        .requestMatchers("/api/pharmacy/orders/**").authenticated()
                        // Other roles
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/pharmacist/**").hasRole("PHARMACIST")
                        .requestMatchers("/laboratory/**").hasRole("LABORATORY_STAFF") // ✅ corrigé
                        .requestMatchers("/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/patient/**").hasRole("PATIENT")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}