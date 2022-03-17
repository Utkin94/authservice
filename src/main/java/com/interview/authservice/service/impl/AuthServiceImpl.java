package com.interview.authservice.service.impl;

import com.interview.authservice.component.JwtUtils;
import com.interview.authservice.entity.User;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.inboun.http.model.JwtTokenPairDto;
import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.TokenRefreshRequest;
import com.interview.authservice.service.AuthService;
import com.interview.authservice.service.UserService;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.interview.authservice.component.JwtUtils.CLAIM_ALIAS_USERNAME;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtTokenPairDto login(LoginRequest loginRequest) {
        //todo no such user?
        var user = userService.getUserByUsername(loginRequest.getUsername());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return createTokenPairByUser(user);
    }

    @Override
    public JwtTokenPairDto refresh(TokenRefreshRequest refreshRequest) {
        //todo test not happy paths
        var oldRefreshToken = refreshRequest.getRefreshToken();

        //todo very bad because, we need to throw underline exception upstairs
        if (!jwtUtils.isTokenValid(oldRefreshToken)) {
            throw new BadCredentialsException("Invalid token");
        }

        var decodedToken = jwtUtils.decodeToken(oldRefreshToken);
        var user = userService.getUserByUsername(decodedToken.getClaim(CLAIM_ALIAS_USERNAME).asString());

        JwtTokenPairDto tokenPairByUser = createTokenPairByUser(user);
        return tokenPairByUser;
    }

    @NotNull
    private JwtTokenPairDto createTokenPairByUser(User user) {
        var accessToken = jwtUtils.createAccessToken(user.getId().toString(), user.getUsername(), getUserAuthorities(user));
        var refreshToken = jwtUtils.createRefreshToken(user.getId().toString(), user.getUsername());

        var jwtTokenPairDto = new JwtTokenPairDto();
        jwtTokenPairDto.setAccessToken(accessToken);
        jwtTokenPairDto.setRefreshToken(refreshToken);
        return jwtTokenPairDto;
    }

    private Set<GrantedAuthority> getUserAuthorities(com.interview.authservice.entity.User user) {
        return Objects.requireNonNull(user.getRoles())
                .stream()
                .map(UserRole::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getRoleKey()))
                .collect(Collectors.toSet());
    }
}
