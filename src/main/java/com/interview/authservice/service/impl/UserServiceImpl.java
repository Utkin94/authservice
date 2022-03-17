package com.interview.authservice.service.impl;

import com.interview.authservice.entity.Role;
import com.interview.authservice.entity.User;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.entity.UserRoleId;
import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.inboun.http.model.UserUpdateRequest;
import com.interview.authservice.repository.RoleRepository;
import com.interview.authservice.repository.UserRepository;
import com.interview.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User createUser(UserCreationRequest userDtoToCreate) {
        var defaultRole = getDefaultRole();
        var userToSave = new User();

        userToSave.setUsername(userDtoToCreate.getUsername());
        userToSave.setFirstName(userDtoToCreate.getFirstName());
        userToSave.setLastName(userDtoToCreate.getLastName());
        userToSave.setPassword(passwordEncoder.encode(userDtoToCreate.getPassword()));
        userToSave.setRoles(Set.of(new UserRole()
                .setId(new UserRoleId(userToSave.getId(), defaultRole.getId()))
                .setUser(userToSave)
                .setRole(defaultRole)
        ));

        return userRepository.save(userToSave);
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        var user = userRepository.findById(userId).orElseThrow();

        //only two fields for simplicity
        if (request.getNewFirstName() != null) {
            user.setFirstName(request.getNewFirstName());
        }
        if (request.getNewLastName() != null) {
            user.setLastName(request.getNewLastName());
        }

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    private Role getDefaultRole() {
        return roleRepository.findAll()
                .stream()
                //todo make it more flexible
                .filter(r -> r.getRoleKey().equals("ROLE_USER"))
                .findFirst().orElseThrow();
    }

}
