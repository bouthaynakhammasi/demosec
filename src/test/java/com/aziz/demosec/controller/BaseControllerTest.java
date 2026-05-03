package com.aziz.demosec.controller;

import com.aziz.demosec.security.CustomUserDetailsService;
import com.aziz.demosec.security.jwt.JwtService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Base class for Controller tests to handle security dependencies.
 */
public abstract class BaseControllerTest {

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected CustomUserDetailsService userDetailsService;

    @MockBean
    protected PasswordEncoder passwordEncoder;
}
