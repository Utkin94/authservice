package com.interview.authservice.service;

import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
