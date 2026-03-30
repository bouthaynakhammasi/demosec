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

                .authorizeHttpRequests(auth -> auth


                        // ✅ OPTIONS preflight - DOIT être en premier
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/home-care-services/**").permitAll()
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()

                        // FORUM - Posts and Comments
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
                        .requestMatchers(HttpMethod.DELETE, "/api/forum/posts/*/like").authenticated()

                         // Doctor & Nutritionist medical access
                        .requestMatchers("/treatment/**").hasAnyRole("DOCTOR", "NUTRITIONIST")
                        .requestMatchers("/diagnosis/**").hasAnyRole("DOCTOR", "NUTRITIONIST")
                        .requestMatchers("/consultation/**").hasAnyRole("DOCTOR", "NUTRITIONIST")
                        .requestMatchers("/prescription/**").hasAnyRole("DOCTOR", "NUTRITIONIST")

                        // Other roles
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/pharmacist/**").hasRole("PHARMACIST")

                        .requestMatchers("/laboratory/**").hasRole("LABORATORY_STAFF") 

                        .requestMatchers("/nutritionist/**").hasRole("NUTRITIONIST")
                        .requestMatchers("/visitor/**").hasRole("VISITOR")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/laboratories/**").permitAll()


                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}