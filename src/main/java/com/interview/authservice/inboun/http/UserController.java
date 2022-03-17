package com.interview.authservice.inboun.http;

import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.inboun.http.model.UserDto;
import com.interview.authservice.inboun.http.model.UserUpdateRequest;
import com.interview.authservice.mapper.UserMapper;
import com.interview.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId.toString())")
    public UserDto updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
        var user = userService.updateUser(userId, request);
        return userMapper.mapUserEntityToDto(user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId.toString())")
    public void deleteUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }
}
