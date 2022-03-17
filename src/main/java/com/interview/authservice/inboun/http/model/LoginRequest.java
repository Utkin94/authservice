package com.interview.authservice.inboun.http.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
