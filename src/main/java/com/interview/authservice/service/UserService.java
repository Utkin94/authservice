package com.interview.authservice.service;

import com.interview.authservice.entity.User;
import com.interview.authservice.inboun.http.model.UserCreationRequest;

public interface UserService {
    User createUser(UserCreationRequest request);
}
