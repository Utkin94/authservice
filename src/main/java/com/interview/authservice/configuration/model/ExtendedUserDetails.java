package com.interview.authservice.configuration.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class ExtendedUserDetails extends User {
    private String userId;

    public ExtendedUserDetails(String username, String password,
                               Collection<? extends GrantedAuthority> authorities,
                               String userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
