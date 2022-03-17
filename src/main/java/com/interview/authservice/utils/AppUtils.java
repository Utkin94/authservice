package com.interview.authservice.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AppUtils {

    public static List<String> authoritiesToStrings(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static List<GrantedAuthority> stringsToAuthorities(List<String> list) {
        return list.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
