package com.interview.authservice.inboun.http;

import com.interview.authservice.inboun.http.model.JwtTokenPairDto;
import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.TokenRefreshRequest;
import com.interview.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtTokenPairDto login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public JwtTokenPairDto refresh(@RequestBody TokenRefreshRequest refreshRequest) {
        return authService.refresh(refreshRequest);
    }
}
