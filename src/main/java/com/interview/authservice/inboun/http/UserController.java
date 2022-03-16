package com.interview.authservice.inboun.http;

import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.inboun.http.model.UserDto;
import com.interview.authservice.mapper.UserMapper;
import com.interview.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@RequestBody UserCreationRequest request) {
        var createdUser = userService.createUser(request);
        return userMapper.mapUserEntityToDto(createdUser);
    }
}
