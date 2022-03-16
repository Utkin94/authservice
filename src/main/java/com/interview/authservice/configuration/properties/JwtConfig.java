package com.interview.authservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtConfig {
    private String secret;
    private String issuer;
    private Long accessExpirationTime;
    private Long refreshExpirationTime;
}