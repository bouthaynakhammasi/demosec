package com.aziz.demosec.security.jwt;

import com.aziz.demosec.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // List of public endpoints to skip JWT
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth",
            "/api/home-care-services",
            "/error"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. Lire le header Authorization
         String authHeader = request.getHeader("Authorization");
         String jwt;
         String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("JWT filter skip: method={}, uri={}, hasAuthHeader={}", request.getMethod(), path, authHeader != null);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail;

        // 2. Extraire et valider le token
        jwt = authHeader.substring(7);
        try {
            // 3️⃣ Extract email from JWT
            userEmail = jwtService.extractEmail(jwt);

            // 4️⃣ Authenticate if not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT valid: subject={}, authorities={}", userEmail, userDetails.getAuthorities());
                } else {
                    log.debug("JWT invalid: subject={}", userEmail);
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expired");
            return;
        } catch (Exception e) {
            // Si le token est malformé ou invalide, on continue simplement la chaine.
            // La sécurité Spring bloquera l'accès plus tard si la route est protégée.
            logger.warn("JWT validation failed: " + e.getMessage());
        }
        // 4. Continuer la chaine
        filterChain.doFilter(request, response);
    }
}
