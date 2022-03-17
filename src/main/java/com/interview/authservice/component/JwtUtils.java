package com.interview.authservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.interview.authservice.configuration.properties.AppProperties;
import com.interview.authservice.configuration.properties.JwtConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

import static com.interview.authservice.utils.AppUtils.authoritiesToStrings;

@Component
public class JwtUtils {

    public static final String CLAIM_ALIAS_USERNAME = "username";
    public static final String CLAIM_ALIAS_ROLES = "roles";

    private final JwtConfig jwtConfig;
    private final Algorithm algorithm;

    public JwtUtils(AppProperties appProperties) {
        this.jwtConfig = appProperties.getJwt();
        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
    }

    public String createAccessToken(String userId, String username, Collection<GrantedAuthority> roles) {
        return createBuilderWithTimes(jwtConfig.getAccessExpirationTime())
                .withSubject(userId)
                .withIssuer(jwtConfig.getIssuer())
                .withClaim(CLAIM_ALIAS_USERNAME, username)
                .withClaim(CLAIM_ALIAS_ROLES, authoritiesToStrings(roles))
                .sign(algorithm);
    }

    public String createRefreshToken(String userId, String username) {
        return createBuilderWithTimes(jwtConfig.getRefreshExpirationTime())
                .withSubject(userId)
                .withIssuer(jwtConfig.getIssuer())
                .withClaim(CLAIM_ALIAS_USERNAME, username)
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String accessToken) {
        return JWT.decode(accessToken);
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }

        return true;
    }

    private JWTCreator.Builder createBuilderWithTimes(long offset) {
        var currentTimeMillis = System.currentTimeMillis();
        var currentTimePlusOffset = currentTimeMillis + offset;

        return JWT.create()
                .withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimePlusOffset));
    }
}
