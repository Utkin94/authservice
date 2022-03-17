package com.interview.authservice.component;


import com.interview.authservice.configuration.properties.AppProperties;
import com.interview.authservice.configuration.properties.JwtConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(jwtUtils.isTokenValid(accessToken));
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

        assertFalse(jwtUtils.isTokenValid(accessToken));
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

        assertFalse(jwtUtils.isTokenValid(accessToken));
    }

}