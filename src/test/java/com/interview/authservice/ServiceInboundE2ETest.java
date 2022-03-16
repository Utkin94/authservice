package com.interview.authservice;

import com.interview.authservice.component.JwtUtils;
import com.interview.authservice.configuration.model.LoginRequest;
import com.interview.authservice.configuration.model.LoginResponse;
import com.interview.authservice.configuration.properties.AppProperties;
import com.interview.authservice.entity.Role;
import com.interview.authservice.entity.User;
import com.interview.authservice.entity.UserRole;
import com.interview.authservice.entity.UserRoleId;
import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.repository.RoleRepository;
import com.interview.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.interview.authservice.component.JwtUtils.CLAIM_ALIAS_ROLES;
import static com.interview.authservice.component.JwtUtils.CLAIM_ALIAS_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceInboundE2ETest extends AbstractE2ETest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    public void beforeEach() {
        executeWithTransaction(() -> {
            var roles = roleRepository.findAll();
            var roleAdmin = roles.stream().filter(r -> r.getRoleKey().equals("ROLE_ADMIN")).findFirst().orElseThrow();
            var roleUser = roles.stream().filter(r -> r.getRoleKey().equals("ROLE_USER")).findFirst().orElseThrow();

            userRepository.deleteAll();
            createUserWithRole(new User()
                    .setUsername("admin")
                    .setFirstName("firstName")
                    .setLastName("lastName")
                    .setPassword("admin"), roleAdmin);

            createUserWithRole(new User()
                    .setUsername("user")
                    .setFirstName("firstName")
                    .setLastName("lastName")
                    .setPassword("user"), roleUser);
        });
    }

    @AfterEach
    public void afterEach() {
        executeWithTransaction(() -> userRepository.deleteAll());
    }

    @Test
    public void postUser_newUserShouldBeCreated() throws Exception {
        var roleUser = roleRepository.findAll().stream().filter(r -> r.getRoleKey().equals("ROLE_USER")).findFirst().orElseThrow();

        var password = "password";
        var userName = "userName";
        var firstName = "firstName";
        var lastName = "lastName";

        var userDtoToCreate = new UserCreationRequest()
                .setUsername(userName)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password);

        //perform request
        mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(userDtoToCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username").value(userName))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

        //validate db data
        assertThat(userRepository.findByUsername(userName).orElseThrow())
                .matches(user -> user.getUsername().equals(userName))
                .matches(user -> user.getFirstName().equals(firstName))
                .matches(user -> user.getLastName().equals(lastName))
                .matches(user -> passwordEncoder.matches(password, user.getPassword()))
                .matches(user -> user.getRoles().stream().findFirst().orElseThrow().getRole().equals(roleUser));
    }

    @Test
    public void postLogin_userShouldBeLogIn() throws Exception {
        var admin = userRepository.findByUsername("admin").orElseThrow();
        var loginRequest = new LoginRequest()
                .setUsername(admin.getUsername())
                .setPassword(admin.getUsername());

        var result = mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        assertThat(jwtUtils.decodeToken(response.getAccessToken()))
                .matches(token -> token.getIssuer().equals(appProperties.getJwt().getIssuer()))
                .matches(token -> admin.getUsername().equals(token.getClaim(CLAIM_ALIAS_USERNAME).asString()))
                .matches(token -> admin.getId().toString().equals(token.getSubject()))
                .matches(token -> rolesToStrings(admin.getRoles()).equals(token.getClaim(CLAIM_ALIAS_ROLES).asList(String.class)));
    }

    private User createUserWithRole(User userToCreate, Role role) {
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getUsername()));
        userToCreate.setRoles(
                Set.of(new UserRole()
                        .setId(new UserRoleId(role.getId(), null))
                        .setRole(role)
                        .setUser(userToCreate)
                )
        );

        return userRepository.save(userToCreate);
    }

    private List<String> rolesToStrings(Set<UserRole> roles) {
        return roles.stream()
                .map(UserRole::getRole)
                .map(Role::getRoleKey)
                .collect(Collectors.toList());
    }
}
