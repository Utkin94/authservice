package com.interview.authservice.component;


import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.interview.authservice.configuration.properties.AppProperties;
import com.interview.authservice.configuration.properties.JwtConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    @Test
    public void createAndVerifyAccessTokenTest() {
        AppProperties appProperties = new AppProperties();
        JwtConfig jwt = new JwtConfig();
        jwt.setSecret("secret");
        jwt.setIssuer("issuer");
        jwt.setAccessExpirationTime(1000L);
        jwt.setRefreshExpirationTime(1000L);
        appProperties.setJwt(jwt);

        JwtUtils jwtUtils = new JwtUtils(appProperties);

        String accessToken = jwtUtils.createAccessToken("1", "username", List.of(new SimpleGrantedAuthority("role")));
        assertDoesNotThrow(() -> jwtUtils.validateToken(accessToken));
    }

    @Test
    public void verifyFakeToken() {
        AppProperties appProperties = new AppProperties();
        JwtConfig jwt = new JwtConfig();
        jwt.setSecret("secret");
        jwt.setIssuer("issuer");
        jwt.setAccessExpirationTime(1L);
        jwt.setRefreshExpirationTime(1L);
        appProperties.setJwt(jwt);

        JwtUtils jwtUtils = new JwtUtils(appProperties);

        appProperties.getJwt().setSecret("fake");
        JwtUtils fakeJwtUtils = new JwtUtils(appProperties);

        String accessToken = fakeJwtUtils.createAccessToken("1", "username", List.of(new SimpleGrantedAuthority("role")));

        assertThrows(SignatureVerificationException.class, () -> jwtUtils.validateToken(accessToken));
    }

    @SneakyThrows
    @Test
    public void verifyExpiredToken() {
        AppProperties appProperties = new AppProperties();
        JwtConfig jwt = new JwtConfig();
        jwt.setSecret("secret");
        jwt.setIssuer("issuer");
        jwt.setAccessExpirationTime(1L);
        jwt.setRefreshExpirationTime(1L);
        appProperties.setJwt(jwt);

        JwtUtils jwtUtils = new JwtUtils(appProperties);

        String accessToken = jwtUtils.createAccessToken("1", "username", List.of(new SimpleGrantedAuthority("role")));

        //todo sorry for this
        Thread.sleep(1000L);

        assertThrows(TokenExpiredException.class, () -> jwtUtils.validateToken(accessToken));
    }

}