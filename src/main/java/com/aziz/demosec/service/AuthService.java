package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;

public interface AuthService {
    User register(RegisterRequest req, String documentUrl);

    AuthResponse login(LoginRequest req);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);
}
