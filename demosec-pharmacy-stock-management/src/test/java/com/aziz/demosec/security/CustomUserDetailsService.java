package com.aziz.demosec.security;

import com.aziz.demosec.repository.UserRepository;
import com.aziz.demosec.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class CustomUserDetailsService implements UserDetailsService {


    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {



        User u = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + u.getRole().name())
        );

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(authorities)
                .disabled(!u.isEnabled())
                .build();
    }
}
