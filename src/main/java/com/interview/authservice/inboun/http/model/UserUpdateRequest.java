package com.interview.authservice.inboun.http.model;

import lombok.Data;

@Data
public class UserUpdateRequest {
    //todo extend
    private String newFirstName;
    private String newLastName;
}
