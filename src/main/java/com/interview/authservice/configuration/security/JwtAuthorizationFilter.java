package com.interview.authservice.configuration.security;

import com.interview.authservice.component.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.interview.authservice.component.JwtUtils.CLAIM_ALIAS_ROLES;
import static com.interview.authservice.utils.AppUtils.stringsToAuthorities;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final String LOGIN_PATH = "api/auth/login";

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!request.getServletPath().equals(LOGIN_PATH)
                && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            tryToSetAuthentication(authorizationHeader);
        }

        filterChain.doFilter(request, response);
    }

    private void tryToSetAuthentication(String authorizationHeader) {
        try {
            var token = authorizationHeader.substring(7);
            if (jwtUtils.isTokenValid(token)) {
                var decodedJWT = jwtUtils.decodeToken(token);
                var roles = decodedJWT.getClaim(CLAIM_ALIAS_ROLES).asList(String.class);

                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        decodedJWT.getSubject(), null, stringsToAuthorities(roles));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error("Error on jwt token validation: ", e);
        }
    }
}
