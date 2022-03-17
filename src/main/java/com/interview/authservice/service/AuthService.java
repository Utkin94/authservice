package com.interview.authservice.service;

import com.interview.authservice.inboun.http.model.JwtTokenPairDto;
import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.TokenRefreshRequest;

public interface AuthService {
    JwtTokenPairDto login(LoginRequest loginRequest);

    JwtTokenPairDto refresh(TokenRefreshRequest refreshRequest);
}
