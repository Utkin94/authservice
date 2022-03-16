package com.interview.authservice.service.impl;

import com.interview.authservice.configuration.model.ExtendedUserDetails;
import com.interview.authservice.entity.User;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.info("There's no user with username {}", username);
            throw new UsernameNotFoundException("There's no user with username: " + username);
        }

        return userToExtendedUserDetails(user);
    }

    private ExtendedUserDetails userToExtendedUserDetails(User user) {
        return new ExtendedUserDetails(user.getUsername(), user.getPassword(), getUserAuthorities(user), user.getId().toString());
    }

    private Set<GrantedAuthority> getUserAuthorities(com.interview.authservice.entity.User user) {
        return Objects.requireNonNull(user.getRoles())
                .stream()
                .map(UserRole::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getRoleKey()))
                .collect(Collectors.toSet());
    }
}
