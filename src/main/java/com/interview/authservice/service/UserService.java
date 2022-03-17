package com.interview.authservice.service;

import com.interview.authservice.entity.User;
import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.inboun.http.model.UserUpdateRequest;

public interface UserService {
    User createUser(UserCreationRequest request);

    User updateUser(Long userId, UserUpdateRequest request);

    void removeUser(Long userId);

    User getUserByUsername(String username);
}
