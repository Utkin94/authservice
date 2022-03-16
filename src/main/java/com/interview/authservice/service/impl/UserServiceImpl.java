package com.interview.authservice.service.impl;

import com.interview.authservice.entity.Role;
import com.interview.authservice.entity.User;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.entity.UserRoleId;
import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.repository.RoleRepository;
import com.interview.authservice.repository.UserRepository;
import com.interview.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public User createUser(UserCreationRequest userDtoToCreate) {
        var defaultRole = getDefaultRole();
        var userToSave = new User();

        userToSave.setUsername(userDtoToCreate.getUsername());
        userToSave.setFirstName(userDtoToCreate.getFirstName());
        userToSave.setLastName(userDtoToCreate.getLastName());
        userToSave.setPassword(new String(userDtoToCreate.getPassword()));
        userToSave.setRoles(Set.of(new UserRole()
                .setId(new UserRoleId(userToSave.getId(), defaultRole.getId()))
                .setUser(userToSave)
                .setRole(defaultRole)
        ));

        return userRepository.save(userToSave);
    }

    private Role getDefaultRole() {
        return roleRepository.findAll()
                .stream()
                //todo make it more flexible
                .filter(r -> r.getRoleKey().equals("ROLE_USER"))
                .findFirst().orElseThrow();
    }

}
