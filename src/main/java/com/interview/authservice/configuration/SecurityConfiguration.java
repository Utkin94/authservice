package com.interview.authservice.configuration;


import com.interview.authservice.configuration.security.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //todo refactor: move access spells to methods
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/auth/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/{userId}")
                .access("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId)");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/users/{userId}")
                .access("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId)");

        http.authorizeRequests().anyRequest().authenticated();
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
