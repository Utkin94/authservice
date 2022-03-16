package com.interview.authservice.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.service")
public class AppProperties {

    @Getter
    @Setter
    private JwtConfig jwt;
}
