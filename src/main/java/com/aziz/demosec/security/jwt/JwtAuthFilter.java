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

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

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
            log.debug("JWT filter skip: method={}, uri={}, hasAuthHeader={}", request.getMethod(), request.getRequestURI(), authHeader != null);
            filterChain.doFilter(request, response);
            return;
        }
        // 2. Extraire le token
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractEmail(jwt);
        log.debug("JWT filter: method={}, uri={}, subject={}", request.getMethod(), request.getRequestURI(), userEmail);
        // 3. Valider le token
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.debug("JWT valid: subject={}, authorities={}", userEmail, userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.debug("JWT invalid: subject={}", userEmail);
            }
        }
        // 4. Continuer la chaine
        filterChain.doFilter(request, response);
    }
}
