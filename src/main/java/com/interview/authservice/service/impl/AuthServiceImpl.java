package com.interview.authservice.service.impl;

import com.interview.authservice.component.JwtUtils;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.LoginResponse;
import com.interview.authservice.service.AuthService;
import com.interview.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        //todo no such user?
        var user = userService.getUserByUsername(loginRequest.getUsername());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var accessToken = jwtUtils.createAccessToken(user.getId().toString(), user.getUsername(), getUserAuthorities(user));
        var refreshToken = jwtUtils.createRefreshToken(user.getId().toString(), user.getUsername());

        var loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;
    }

    private Set<GrantedAuthority> getUserAuthorities(com.interview.authservice.entity.User user) {
        return Objects.requireNonNull(user.getRoles())
                .stream()
                .map(UserRole::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getRoleKey()))
                .collect(Collectors.toSet());
    }
}
