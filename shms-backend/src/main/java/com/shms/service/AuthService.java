package com.shms.service;

import com.shms.dto.JwtResponse;
import com.shms.dto.LoginRequest;
import com.shms.dto.RegisterRequest;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    JwtResponse refresh(String refreshToken);
}
