package com.interview.authservice.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.authservice.component.JwtUtils;
import com.interview.authservice.configuration.model.ExtendedUserDetails;
import com.interview.authservice.configuration.model.LoginRequest;
import com.interview.authservice.configuration.model.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class UsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        if (HttpMethod.POST.name().equals(request.getMethod())) {
            try {
                var authRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

                return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            } catch (Exception e) {
                log.error("Error on authentication attempt. ", e);
            }
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        var userDetails = (ExtendedUserDetails) authResult.getPrincipal();
        var loginResponse = new LoginResponse();

        loginResponse.setAccessToken(jwtUtils.createAccessToken(
                userDetails.getUserId(), userDetails.getUsername(), userDetails.getAuthorities()));

        loginResponse.setRefreshToken(jwtUtils.createRefreshToken(userDetails.getUserId(), userDetails.getUsername()));

        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), loginResponse);
    }
}
