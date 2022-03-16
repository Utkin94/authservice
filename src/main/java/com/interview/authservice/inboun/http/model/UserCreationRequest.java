package com.interview.authservice.inboun.http.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserCreationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private char[] password;
}
