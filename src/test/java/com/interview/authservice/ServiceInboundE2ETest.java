package com.interview.authservice;

import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.repository.RoleRepository;
import com.interview.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @Transactional
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
                .setPassword(password.toCharArray());

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
                //todo password should be encrypted
                .matches(user -> user.getPassword().equals(password))
                .matches(user -> user.getRoles().stream().findFirst().orElseThrow().getRole().equals(roleUser));
    }
    
}
