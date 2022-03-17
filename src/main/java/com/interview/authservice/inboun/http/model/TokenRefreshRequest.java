package com.interview.authservice.inboun.http.model;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
