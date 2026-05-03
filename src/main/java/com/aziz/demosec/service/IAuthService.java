package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.AuthResponse;
import com.aziz.demosec.dto.LoginRequest;
import com.aziz.demosec.dto.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}
