package com.interview.authservice.inboun.http.model;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}
