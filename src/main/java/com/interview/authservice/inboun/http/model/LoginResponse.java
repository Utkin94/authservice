package com.interview.authservice.inboun.http.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
