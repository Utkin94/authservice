package com.interview.authservice.configuration.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequest {
    private String username;
    private String password;
}
